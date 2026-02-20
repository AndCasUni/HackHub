package it.hackhub.observer;

import it.hackhub.model.domain.*;
import it.hackhub.model.enums.NotificationType;
import it.hackhub.service.NotificationService;
import java.time.LocalDateTime;

public class NotificationObserver implements HackathonObserver {

    private final NotificationService notificationService;

    public NotificationObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onStatusChanged(Hackathon hackathon) {
        String message = "Hackathon " + hackathon.getName() + ": ora in fase " + hackathon.getState();
        // Invia a tutti i membri dei team iscritti e allo staff
        hackathon.getStaff().forEach(u -> createNotification(u, "Cambio Stato", message));
    }

    @Override
    public void onStaffAssigned(Hackathon hackathon, User staffMember) {
        String message = "Sei stato assegnato come " + staffMember.getRoleEnum() + " per l'Hackathon " + hackathon.getName();
        createNotification(staffMember, "Nuovo Incarico", message);
    }

    @Override
    public void onInvitationUpdated(TeamInvitation invitation) {
        String message;
        User recipient;

        switch (invitation.getStatus()) {
            case PENDING -> {
                message = "Il Leader del Team " + invitation.getTeam().getName() + " ti ha invitato";
                recipient = invitation.getReceiver();
            }
            case ACCEPTED -> {
                message = invitation.getReceiver().getUsername() + " ha accettato il tuo invito";
                recipient = invitation.getSender();
            }
            case REJECTED -> {
                message = invitation.getReceiver().getUsername() + " ha rifiutato il tuo invito";
                recipient = invitation.getSender();
            }
            default -> {return; }
        }
        createNotification(recipient, "Aggiornamento Invito", message);
    }

    private void createNotification(User user, String title, String msg) {
        Notification n = new Notification();
        n.setRecipient(user);
        n.setTitle(title);
        n.setMessage(msg);
        n.setCreatedAt(LocalDateTime.now());
        notificationService.save(n);
    }
}