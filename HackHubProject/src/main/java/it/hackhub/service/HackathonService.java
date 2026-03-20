package it.hackhub.service;

import it.hackhub.exception.InvalidHackathonStateException;
import it.hackhub.exception.StaffMemberAlreadyOccupiedException;
import it.hackhub.exception.UserAlreadyStaffException;
import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.observer.HackathonSubject;
import it.hackhub.repository.HackathonRepository;
import it.hackhub.repository.PaymentRepository;
import it.hackhub.repository.TeamRepository;
import it.hackhub.repository.UserRepository;
import it.hackhub.state.RegistrationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.hackhub.state.EvaluationState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HackathonService extends HackathonSubject {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    @Autowired
    private final TeamRepository teamRepository;

    @Autowired
    public HackathonService(HackathonRepository hackathonRepository,
                            UserRepository userRepository,
                            PaymentRepository paymentRepository,
                            TeamRepository teamRepository) {
        this.hackathonRepository = hackathonRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public Hackathon createHackathon(String customId, String name, String description,
                                     LocalDateTime startDate, LocalDateTime endDate,
                                     Double prizeAmount, String organizerId, Integer maxParticipants) {

        // Recupera l'organizzatore
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new NoSuchElementException("Organizzatore non trovato con ID: " + organizerId));

        if (organizer.getRoleEnum() != UserRoleEnum.ORGANIZER) {
            throw new IllegalArgumentException("L'utente specificato non è un ORGANIZER");
        }

        Hackathon hackathon = new Hackathon();

        if (customId != null && !customId.isBlank()) {
            hackathon.setId(customId);
        }

        hackathon.setName(name);
        hackathon.setDescription(description);
        hackathon.setStartDate(startDate);
        hackathon.setEndDate(endDate);
        hackathon.setPrizeAmount(prizeAmount);
        hackathon.setOrganizer(organizer);
        hackathon.setMaxParticipants(maxParticipants);

        hackathon.changeState(new RegistrationState());

        return hackathonRepository.save(hackathon);
    }

    @Transactional
    public void addStaff(String hackathonId, String userId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new NoSuchElementException("Hackathon non trovato"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Utente non trovato"));

        // Controlla se è già staff QUI
        boolean alreadyStaff = hackathon.getStaff().stream()
                .anyMatch(u -> u.getId().equals(userId));


        if (alreadyStaff) {
            throw new StaffMemberAlreadyOccupiedException(userId);        }

        hackathon.getStaff().add(user);
        user.getAssignedHackathons().add(hackathon);

        hackathonRepository.save(hackathon);
        userRepository.save(user);

        notifyStaffAssigned(hackathon, user);
    }

    @Transactional
    public void registerTeam(String hackathonId, String teamId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new NoSuchElementException("Hackathon non trovato"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team non trovato"));

        if (hackathon.getState() != HackathonStatus.REGISTRATION) {
            throw new InvalidHackathonStateException("L'hackathon non è in fase di registrazione");
        }

        if (team.getRegisteredHackathon() != null) {
            throw new IllegalStateException("Il team è già iscritto a un hackathon");
        }

        if (team.getMembers().size() > hackathon.getMaxParticipants()) {
            throw new IllegalStateException("Il team è troppo grande per questo hackathon. Massimo consentito: " + hackathon.getMaxParticipants());
        }
        team.setRegisteredHackathon(hackathon);
        hackathon.getRegisteredTeams().add(team);

        teamRepository.save(team);
        hackathonRepository.save(hackathon);
    }

    @Transactional
    public void startHackathon(String hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new NoSuchElementException("Hackathon non trovato"));

        // Deve essere in fase di registrazione
        if (hackathon.getState() != HackathonStatus.REGISTRATION) {
            throw new IllegalStateException("Impossibile iniziare: l'hackathon non è più in fase di registrazione.");
        }
        hackathon.getCurrentStateObject().transitionToOngoing(hackathon);

        hackathonRepository.save(hackathon);
        notifyStatusChange(hackathon);
    }

    @Transactional
    public void declareWinner(String hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new NoSuchElementException("Hackathon non trovato"));

        Team winner = hackathon.calculateWinner();

        if (winner != null) {
            hackathon.setWinner(winner);

            hackathon.getCurrentStateObject().declareWinner(hackathon);

            // Pagamento
            paymentRepository.savePaymentRecord(winner.getId(),
                    hackathon.getPrizeAmount() != null ? hackathon.getPrizeAmount() : 0.0,
                    hackathon.getId());

            // 2. INVIA LA NOTIFICA DELLA VITTORIA
            notifyTeamWon(hackathon, winner);
        } else {
            // Se non ci sono vincitori (es. zero team)
            hackathon.changeState(new it.hackhub.state.CompletedState());
        }

        hackathonRepository.save(hackathon);
        notifyStatusChange(hackathon); // Notifica generica di hackathon concluso
    }


    @Transactional
    public void forceStateToEvaluation(String id) {
        Hackathon hackathon = getHackathonById(id);

        hackathon.changeState(new EvaluationState());

        hackathonRepository.save(hackathon);

        notifyStatusChange(hackathon);
    }

    public List<Hackathon> getAllHackathons() {
        return hackathonRepository.findAll();
    }

    public Hackathon getHackathonById(String id) {
        return hackathonRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Hackathon non trovato con ID: " + id));
    }

    @Transactional
    public void deleteHackathon(String hackathonId, String requesterId) {
        Hackathon hackathon = getHackathonById(hackathonId);

        // L'utente richiedente deve essere l'organizzatore
        if (!hackathon.getOrganizer().getId().equals(requesterId)) {
            throw new SecurityException("Solo l'organizzatore dell'hackathon può cancellare l'evento.");
        }

        // Deve essere in fase di registrazione
        if (hackathon.getState() != HackathonStatus.REGISTRATION) {
            throw new IllegalStateException("Impossibile cancellare: l'hackathon non è più in fase di registrazione.");
        }

        // Deve avere 0 team iscritti
        if (hackathon.getRegisteredTeams() != null && !hackathon.getRegisteredTeams().isEmpty()) {
            throw new IllegalStateException("Impossibile cancellare l'hackathon: ci sono già team iscritti. Falli disiscrivere prima.");
        }

        hackathonRepository.delete(hackathon);
    }

    // MOSTRA PER STATO
    public List<Hackathon> getHackathonsByState(HackathonStatus state) {
        return hackathonRepository.findByState(state);
    }

    // SEGNALA VIOLAZIONE (Solo Mentore)
    @Transactional
    public void reportTeamViolation(String mentorId, String teamId, String reason) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new NoSuchElementException("Mentore non trovato"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team non trovato"));

        if (mentor.getRoleEnum() != UserRoleEnum.MENTOR) {
            throw new SecurityException("Solo un MENTOR può segnalare violazioni.");
        }


        team.setReported(true);
        team.setReportReason(reason);
        teamRepository.save(team);
    }

    // SQUALIFICA TEAM (Solo Organizer, Solo su segnalazione)
    @Transactional
    public void disqualifyTeam(String organizerId, String teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team non trovato"));

        Hackathon hackathon = team.getRegisteredHackathon();
        if (hackathon == null) throw new NoSuchElementException("Il team non è iscritto a nessun hackathon.");

        // Richiedente deve essere l'organizzatore dell'hackathon
        if (!hackathon.getOrganizer().getId().equals(organizerId)) {
            throw new SecurityException("Solo l'organizzatore dell'hackathon può squalificare un team.");
        }

        // Deve esserci una segnalazione del mentore
        if (!team.isReported()) {
            throw new IllegalArgumentException("Impossibile squalificare: Il team non ha ricevuto segnalazioni da un Mentore.");
        }

        team.setDisqualified(true);
        teamRepository.save(team);
    }
}