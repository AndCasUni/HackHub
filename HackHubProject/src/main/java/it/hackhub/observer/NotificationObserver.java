package it.hackhub.observer;

import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.TeamInvitation;
import it.hackhub.model.domain.User;
import it.hackhub.service.NotificationService;

public class NotificationObserver implements HackathonObserver, InvitationObserver {

    private final NotificationService notificationService;

    public NotificationObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onHackathonStatusChanged(Hackathon hackathon) {
        String message = "L'Hackathon '" + hackathon.getName() + "' è ora nello stato: " + hackathon.getState();

        if (hackathon.getRegisteredTeams() != null) {
            hackathon.getRegisteredTeams().forEach(team -> {
                // Notifica al leader del team
                notificationService.sendNotification(
                        team.getLeader(),
                        "Aggiornamento Hackathon",
                        message,
                        "INFO"
                );
            });
        }
    }

    @Override
    public void onStaffAssigned(Hackathon hackathon, User staffMember) {
        // Notifica specifica per il membro dello staff assegnato
        String title = "Nuova Assegnazione Staff";
        String message = "Sei stato assegnato come " + staffMember.getRoleEnum() +
                " all'hackathon '" + hackathon.getName() + "'.";

        notificationService.sendNotification(staffMember, title, message, "STAFF_ASSIGNMENT");
    }

    @Override
    public void onInvitationSent(TeamInvitation invitation) {
        notificationService.sendNotification(
                invitation.getReceiver(),
                "Nuovo Invito al Team",
                "Hai ricevuto un invito per unirti al team " + invitation.getTeam().getName(),
                "INVITATION"
        );
    }

    @Override
    public void onInvitationReplied(TeamInvitation invitation) {
        notificationService.sendNotification(
                invitation.getSender(),
                "Risposta Invito",
                "L'utente " + invitation.getReceiver().getUsername() + " ha " + invitation.getStatus() + " il tuo invito.",
                "INVITATION_REPLY"
        );
    }

    @Override
    public void onTeamWon(Hackathon hackathon, Team winningTeam) {
        String title = "🏆 Vittoria Hackathon!";
        String message = String.format("Congratulazioni! Il tuo team '%s' ha vinto l'hackathon '%s' e si è aggiudicato un premio di %.2f€!",
                winningTeam.getName(), hackathon.getName(), hackathon.getPrizeAmount());

        if (winningTeam.getLeader() != null) {
            notificationService.sendNotification(
                    winningTeam.getLeader(),
                    title,
                    message,
                    "SYSTEM"
            );
        }

        // Manda la notifica a tutti i Membri
        if (winningTeam.getMembers() != null) {
            for (User member : winningTeam.getMembers()) {
                notificationService.sendNotification(
                        member,
                        title,
                        message,
                        "SYSTEM"
                );
            }
        }
    }
}