package it.hackhub.service;

import it.hackhub.exception.InvitationAlreadyHandledException;
import it.hackhub.exception.UserAlreadyInTeamException;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.TeamInvitation;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.InvitationStatus;
import it.hackhub.observer.HackathonSubject;
import it.hackhub.repository.TeamInvitationRepository;
import it.hackhub.repository.TeamRepository;
import it.hackhub.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service per la gestione degli inviti ai team.
 * Implementa la logica di business relativa alla partecipazione degli utenti ai team[cite: 25, 26].
 */
public class InvitationService extends HackathonSubject {

    private final TeamInvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public InvitationService(TeamInvitationRepository invitationRepository,
                             UserRepository userRepository,
                             TeamRepository teamRepository) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Invia un invito a un utente per unirsi a un team.
     */
    public TeamInvitation inviteUser(String senderId, String receiverId, String teamId) {
        User sender = userRepository.findById(senderId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team non trovato con ID: " + teamId));
        // Verifica che il ricevente non sia già in un team attivo
        if (receiver.isMemberOfActiveTeam()) {
            throw new UserAlreadyInTeamException(receiverId);
        }

        TeamInvitation invitation = new TeamInvitation();
        invitation.setSender(sender);
        invitation.setReceiver(receiver);
        invitation.setTeam(team);
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setSentAt(LocalDateTime.now());

        invitationRepository.save(invitation);
        notifyInvitation(invitation);
        return invitation;
    }

    /**
     * Accetta un invito. Implementa il vincolo del team unico e
     * il rifiuto automatico degli altri inviti pendenti[cite: 47, 48].
     */
    public void acceptInvitation(String invitationId) {
        TeamInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invito non trovato con ID: " + invitationId));
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvitationAlreadyHandledException(invitationId);
        }

        User receiver = invitation.getReceiver();

        if (receiver.isMemberOfActiveTeam()) {
            throw new UserAlreadyInTeamException(receiver.getId());
        }

        Team team = invitation.getTeam();
        // Usiamo il metodo helper che abbiamo aggiunto alla classe Team
        team.addMember(receiver);
        invitation.setStatus(InvitationStatus.ACCEPTED);

        // Pulizia automatica degli altri inviti pendenti (Requisito Matrix)
        List<TeamInvitation> otherPending = invitationRepository.findPendingByReceiver(receiver.getId());
        for (TeamInvitation other : otherPending) {
            if (!other.getId().equals(invitationId)) {
                other.setStatus(InvitationStatus.REJECTED);
            }
        }

        teamRepository.save(team);
        invitationRepository.save(invitation);
        notifyInvitation(invitation);
    }

    /**
     * Rifiuta un invito specifico[cite: 49].
     */
    public void rejectInvitation(String invitationId) {
        TeamInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invito non trovato con ID: " + invitationId));
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvitationAlreadyHandledException(invitationId);
        }

        invitation.setStatus(InvitationStatus.REJECTED);
        invitationRepository.save(invitation);
        notifyInvitation(invitation);
    }

    /**
     * Recupera la lista di inviti pendenti per un utente[cite: 46].
     */
    public List<TeamInvitation> getPendingInvitations(String userId) {
        return invitationRepository.findPendingByReceiver(userId);
    }
}