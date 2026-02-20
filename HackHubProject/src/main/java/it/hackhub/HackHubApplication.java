package it.hackhub;

import it.hackhub.model.domain.*;
import it.hackhub.model.enums.*;
import it.hackhub.repository.*;
import it.hackhub.service.*;
import it.hackhub.observer.NotificationObserver;
import java.time.LocalDateTime;

public class HackHubApplication {
    public static void main(String[] args) {
        System.out.println("=== Inizializzazione HackHub System ===");

        // 1. Inizializzazione Repository
        UserRepository userRepo = new UserRepository();
        HackathonRepository hackathonRepo = new HackathonRepository();
        TeamRepository teamRepo = new TeamRepository();
        SubmissionRepository submissionRepo = new SubmissionRepository();
        EvaluationRepository evaluationRepo = new EvaluationRepository();
        NotificationRepository notificationRepo = new NotificationRepository();
        TeamInvitationRepository invitationRepo = new TeamInvitationRepository();
        SupportRequestRepository supportRepo = new SupportRequestRepository();
        PaymentRepository paymentRepo = new PaymentRepository();

        // 2. Inizializzazione Service
        UserService userService = new UserService(userRepo);

        // ATTENZIONE: Controlla se il tuo NotificationService richiede anche userRepo nel costruttore
        // Se nel tuo codice è NotificationService(notificationRepo), cancella ", userRepo" qui sotto.
        NotificationService notificationService = new NotificationService(notificationRepo, userRepo);

        // Creazione Observer
        NotificationObserver observer = new NotificationObserver(notificationService);

        HackathonService hackathonService = new HackathonService(hackathonRepo, userRepo, paymentRepo);
        hackathonService.addObserver(observer);

        InvitationService invitationService = new InvitationService(invitationRepo, userRepo, teamRepo);
        invitationService.addObserver(observer);

        TeamService teamService = new TeamService(teamRepo, userRepo, hackathonRepo);
        SubmissionService submissionService = new SubmissionService(submissionRepo, teamRepo);
        EvaluationService evaluationService = new EvaluationService(evaluationRepo, submissionRepo, userRepo);
        
    }

}