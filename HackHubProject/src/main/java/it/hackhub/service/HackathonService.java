package it.hackhub.service;

import it.hackhub.exception.StaffMemberAlreadyOccupiedException;
import it.hackhub.exception.UserAlreadyStaffException;
import it.hackhub.model.domain.*;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.observer.HackathonSubject;
import it.hackhub.repository.*;
import it.hackhub.state.HackathonState;
import it.hackhub.state.RegistrationState;

public class HackathonService extends HackathonSubject {
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public HackathonService(HackathonRepository hackathonRepository, UserRepository userRepository, PaymentRepository paymentRepository) {
        this.hackathonRepository = hackathonRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    // Transizione verso ONGOING (chiamata dall'Organizer)
    public void startHackathon(String hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId);
        // Delega allo stato attuale la logica di transizione
        hackathon.getCurrentStateObject().transitionToOngoing(hackathon);
        hackathonRepository.save(hackathon);

        notifyStatusChange(hackathon);
    }

    // Registrazione di un team
    public void registerTeamToHackathon(String hackathonId, Team team) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId);
        HackathonState state = hackathon.getCurrentStateObject();

        if (state.canRegisterTeam()) {
            state.registerTeam(hackathon, team);
            hackathonRepository.save(hackathon);
        } else {
            throw new IllegalStateException("Registrazione non permessa nello stato: " + hackathon.getState());
        }
    }

    public void addStaff(String hackathonId, String userId) {
        // Recupero dell'hackathon e dell'utente con gestione Optional
        Hackathon hackathon = hackathonRepository.findById(hackathonId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // 1. Controllo dello stato dell'hackathon (deve essere in REGISTRATION)
        if (!hackathon.getCurrentStateObject().canAssignStaff()) {
            throw new IllegalStateException("Assegnazione staff permessa solo in fase di registrazione.");
        }

        // 2. Controllo se l'utente è già assegnato a QUESTO hackathon
        if (hackathon.getStaff().contains(user)) {
            throw new UserAlreadyStaffException(userId, hackathonId);
        }

        // 3. VINCOLO: Verifica se lo staffer è occupato altrove [cite: 65, 124]
        if (user.isStaffOccupied()) {
            throw new StaffMemberAlreadyOccupiedException(userId);
        }

        // 4. Assegnazione bidirezionale
        hackathon.addStaffMember(user);

        hackathonRepository.save(hackathon);
        notifyStaffAssignment(hackathon, user);
    }

    public void declareWinner(String hackathonId, String organizerId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId);

        // 1. Verifica che chi chiama l'azione sia l'organizzatore (opzionale se gestito da controller)

        // 2. Verifica lo stato tramite il Pattern State (deve essere EVALUATION)
        if (!hackathon.getCurrentStateObject().canDeclareWinner()) {
            throw new IllegalStateException("Si può dichiarare un vincitore solo in fase di valutazione.");
        }

        // 3. Verifica che tutte le sottomissioni siano state giudicate
        if (!hackathon.allSubmissionsJudged()) {
            throw new IllegalStateException("Impossibile procedere: ci sono ancora sottomissioni senza valutazione.");
        }

        // 4. Calcolo del vincitore basato sulla media (Logica Automatica)
        Team winner = hackathon.getRegisteredTeams().stream()
                .filter(t -> t.getLatestSubmission() != null)
                .max(java.util.Comparator.comparingDouble(t -> t.getLatestSubmission().getAverageScore()))
                .orElseThrow(() -> new RuntimeException("Nessun team con sottomissione trovato"));

        // 5. Transizione di stato e salvataggio
        hackathon.getCurrentStateObject().declareWinner(hackathon);

        paymentRepository.savePaymentRecord(winner.getId(), hackathon.getPrizeAmount(), hackathon.getId());

        hackathonRepository.save(hackathon);
        notifyStatusChange(hackathon);
    }

    /**
     * Chiude un hackathon se è ancora in fase di registrazione e non ha team iscritti.
     */
    public void closeEmptyHackathon(String hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId);

        // 1. Verifica che lo stato sia REGISTRATION tramite il Pattern State
        if (!(hackathon.getCurrentStateObject() instanceof RegistrationState)) {
            throw new IllegalStateException("L'hackathon può essere annullato senza team solo in fase di registrazione.");
        }

        // 2. Vincolo: Verifica che non ci siano team iscritti
        if (hackathon.getRegisteredTeams() != null && !hackathon.getRegisteredTeams().isEmpty()) {
            throw new IllegalStateException("Impossibile chiudere: ci sono team già iscritti. Procedere con l'evento o gestire i rimborsi.");
        }

        // 3. Esegue la transizione
        hackathon.getCurrentStateObject().cancelHackathon(hackathon);

        // 4. Persiste il cambio di stato
        hackathonRepository.save(hackathon);

        // 5. Trigger Notifica (Observer)
        notifyStatusChange(hackathon);
    }
}