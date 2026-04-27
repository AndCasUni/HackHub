package it.hackhub.service;

import it.hackhub.exception.InvalidHackathonStateException;
import it.hackhub.exception.StaffMemberAlreadyOccupiedException;
import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.User;
import it.hackhub.model.domain.UserStaff;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.observer.HackathonSubject;
import it.hackhub.observer.NotificationObserver;
import it.hackhub.repository.HackathonRepository;
import it.hackhub.repository.PaymentRepository;
import it.hackhub.repository.TeamRepository;
import it.hackhub.repository.UserRepository;
import it.hackhub.state.CompletedState;
import it.hackhub.state.EvaluationState;
import it.hackhub.state.RegistrationState;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HackathonService extends HackathonSubject {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final TeamRepository teamRepository;

    @Autowired
    private NotificationService notificationService;

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

    @PostConstruct
    public void init() {
        this.addObserver(new NotificationObserver(notificationService));
    }

    private Hackathon findHackathon(String id) {
        return hackathonRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Hackathon non trovato con ID: " + id));
    }

    private User findUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utente non trovato con ID: " + id));
    }

    private Team findTeam(String id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Team non trovato con ID: " + id));
    }

    private void checkIsOrganizer(Hackathon hackathon, String requesterId) {
        if (!hackathon.getOrganizer().getId().equals(requesterId))
            throw new SecurityException("Solo l'organizzatore può eseguire questa operazione.");
    }

    @Transactional
    public Hackathon createHackathon(String customId, String name, String description,
                                     LocalDateTime startDate, LocalDateTime endDate,
                                     Double prizeAmount, String organizerId, Integer maxParticipants) {
        User organizer = findUser(organizerId);
        if (organizer.getRoleEnum() != UserRoleEnum.ORGANIZER)
            throw new IllegalArgumentException("L'utente specificato non è un ORGANIZER.");

        Hackathon hackathon = new Hackathon();
        if (customId != null && !customId.isBlank()) hackathon.setId(customId);
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
        Hackathon hackathon = findHackathon(hackathonId);
        User user = findUser(userId);

        if (!(user instanceof UserStaff staffUser))
            throw new IllegalArgumentException("Solo utenti STAFF (JUDGE/MENTOR) possono essere aggiunti.");

        if (staffUser.isStaffOccupied())
            throw new StaffMemberAlreadyOccupiedException(userId);

        if (staffUser.getRoleEnum() == UserRoleEnum.ORGANIZER)
            throw new IllegalArgumentException("L'organizzatore non può essere aggiunto come staff.");

        if (staffUser.getRoleEnum() == UserRoleEnum.JUDGE) {
            boolean judgeGiaPresente = hackathon.getStaff().stream()
                    .anyMatch(s -> s.getRoleEnum() == UserRoleEnum.JUDGE);
            if (judgeGiaPresente)
                throw new IllegalStateException("È già presente un Giudice per questo hackathon. È consentito un solo Giudice.");
        }

        staffUser.setCurrentHackathon(hackathon);
        hackathon.getStaff().add(staffUser);
        userRepository.save(staffUser);
        hackathonRepository.save(hackathon);
        notifyStaffAssigned(hackathon, user);
    }

    @Transactional
    public void registerTeam(String hackathonId, String teamId) {
        Hackathon hackathon = findHackathon(hackathonId);
        Team team = findTeam(teamId);
        if (hackathon.getState() != HackathonStatus.REGISTRATION)
            throw new InvalidHackathonStateException("L'hackathon non è in fase di registrazione.");
        if (team.getRegisteredHackathon() != null)
            throw new IllegalStateException("Il team è già iscritto a un hackathon.");
        if (team.getMembers().size() > hackathon.getMaxParticipants())
            throw new IllegalStateException("Il team è troppo grande. Massimo: " + hackathon.getMaxParticipants());

        team.setRegisteredHackathon(hackathon);
        hackathon.getRegisteredTeams().add(team);
        teamRepository.save(team);
        hackathonRepository.save(hackathon);
    }

    @Transactional
    public void startHackathon(String hackathonId, String requesterId) {
        Hackathon hackathon = findHackathon(hackathonId);
        checkIsOrganizer(hackathon, requesterId);

        if (hackathon.getState() != HackathonStatus.REGISTRATION)
            throw new IllegalStateException("Impossibile avviare: l'hackathon non è in fase di registrazione.");

        boolean hasJudge = hackathon.getStaff().stream()
                .anyMatch(s -> s.getRoleEnum() == UserRoleEnum.JUDGE);
        if (!hasJudge)
            throw new IllegalStateException("Impossibile avviare: nessun Giudice assegnato allo staff.");

        boolean hasMentor = hackathon.getStaff().stream()
                .anyMatch(s -> s.getRoleEnum() == UserRoleEnum.MENTOR);
        if (!hasMentor)
            throw new IllegalStateException("Impossibile avviare: nessun Mentore assegnato allo staff.");

        if (hackathon.getRegisteredTeams() == null || hackathon.getRegisteredTeams().isEmpty())
            throw new IllegalStateException("Impossibile avviare: nessun team iscritto alla competizione.");

        hackathon.getCurrentStateObject().transitionToOngoing(hackathon);
        hackathonRepository.save(hackathon);

        notifyStatusChange(hackathon);
    }

    @Transactional
    public void forceStateToEvaluation(String hackathonId, String requesterId) {
        Hackathon hackathon = findHackathon(hackathonId);
        checkIsOrganizer(hackathon, requesterId);
        hackathon.changeState(new EvaluationState());
        hackathonRepository.save(hackathon);
        notifyStatusChange(hackathon);
    }

    @Transactional
    public void declareWinner(String hackathonId, String requesterId) {
        Hackathon hackathon = findHackathon(hackathonId);
        checkIsOrganizer(hackathon, requesterId);

        if (hackathon.getState() != HackathonStatus.EVALUATION)
            throw new IllegalStateException("Impossibile dichiarare il vincitore: l'hackathon non è in fase di valutazione.");

        Team winner = hackathon.calculateWinner();

        if (winner != null) {
            hackathon.setWinner(winner);
            hackathon.getCurrentStateObject().declareWinner(hackathon);
            paymentRepository.savePaymentRecord(
                    winner.getId(),
                    hackathon.getPrizeAmount() != null ? hackathon.getPrizeAmount() : 0.0,
                    hackathon.getId());
            notifyTeamWon(hackathon, winner);
        } else {
            hackathon.changeState(new CompletedState());
            hackathonRepository.save(hackathon);
            notifyStatusChange(hackathon);
        }

        hackathonRepository.save(hackathon);
        hackathon.getStaff().forEach(s -> s.setCurrentHackathon(null));
        userRepository.saveAll(hackathon.getStaff());

        hackathon.getRegisteredTeams().forEach(t -> {
            t.setRegisteredHackathon(null);
            teamRepository.save(t);
        });
    }

    public List<Hackathon> getAllHackathons() {
        return hackathonRepository.findAll();
    }

    public Hackathon getHackathonById(String id) {
        return findHackathon(id);
    }

    @Transactional
    public void deleteHackathon(String hackathonId, String requesterId) {
        Hackathon hackathon = findHackathon(hackathonId);
        checkIsOrganizer(hackathon, requesterId);
        if (hackathon.getState() != HackathonStatus.REGISTRATION)
            throw new IllegalStateException("Impossibile cancellare: l'hackathon non è in fase di registrazione.");
        if (hackathon.getRegisteredTeams() != null && !hackathon.getRegisteredTeams().isEmpty())
            throw new IllegalStateException("Impossibile cancellare: ci sono team iscritti.");
        hackathon.getStaff().forEach(s -> s.setCurrentHackathon(null));
        userRepository.saveAll(hackathon.getStaff());
        hackathonRepository.delete(hackathon);
    }

    public List<Hackathon> getHackathonsByState(HackathonStatus state) {
        return hackathonRepository.findByState(state);
    }

    @Transactional
    public void reportTeamViolation(String mentorId, String teamId, String reason) {
        User mentor = findUser(mentorId);
        Team team = findTeam(teamId);

        if (mentor.getRoleEnum() != UserRoleEnum.MENTOR)
            throw new SecurityException("Solo un MENTOR può segnalare violazioni.");

        Hackathon hackathon = team.getRegisteredHackathon();

        if (hackathon == null)
            throw new IllegalStateException("Il team non è iscritto a nessun hackathon.");

        boolean isStaff = hackathon.getStaff().stream()
                .anyMatch(s -> s.getId().equals(mentorId));
        if (!isStaff)
            throw new SecurityException("Il mentore non è assegnato a questo hackathon.");

        if (team.isReported())
            throw new IllegalStateException("Il team è già stato segnalato.");

        team.setReported(true);
        team.setReportReason(reason);
        teamRepository.save(team);

        User organizer = hackathon.getOrganizer();
        notificationService.sendNotification(
                organizer,
                "Segnalazione violazione",
                "Il team " + team.getName() + " è stato segnalato dal mentore "
                        + mentor.getUsername() + ". Motivo: " + reason,
                "VIOLATION_REPORTED");
    }

    @Transactional
    public void disqualifyTeam(String organizerId, String teamId) {
        Team team = findTeam(teamId);
        Hackathon hackathon = team.getRegisteredHackathon();
        if (hackathon == null)
            throw new NoSuchElementException("Il team non è iscritto a nessun hackathon.");
        checkIsOrganizer(hackathon, organizerId);
        if (!team.isReported())
            throw new IllegalArgumentException("Il team non ha ricevuto segnalazioni da un Mentore.");

        team.setDisqualified(true);
        teamRepository.save(team);
    }
}