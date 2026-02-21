package it.hackhub.service;

import it.hackhub.exception.InvitationAlreadyHandledException;
import it.hackhub.exception.UserAlreadyInTeamException;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.TeamInvitation;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.InvitationStatus;
import it.hackhub.observer.InvitationSubject;
import it.hackhub.repository.TeamInvitationRepository;
import it.hackhub.repository.TeamRepository;
import it.hackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.hackhub.model.enums.InvitationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class InvitationService extends InvitationSubject {

    private final TeamInvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public InvitationService(TeamInvitationRepository invitationRepository,
                             UserRepository userRepository,
                             TeamRepository teamRepository) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public void inviteUser(String senderId, String receiverId, String teamId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NoSuchElementException("Mittente non trovato"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NoSuchElementException("Destinatario non trovato"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team non trovato"));

        if (team.getLeader() == null || !team.getLeader().getId().equals(senderId)) {
            throw new SecurityException("Azione non consentita: solo il leader del team può inviare inviti.");
        }

        // Controllo se l'utente è già in un team
        if (userRepository.isUserInAnyTeam(receiverId)) {
            throw new UserAlreadyInTeamException(receiver.getUsername());
        }

        TeamInvitation invitation = new TeamInvitation();
        invitation.setSender(sender);
        invitation.setReceiver(receiver);
        invitation.setTeam(team);
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setSentAt(LocalDateTime.now());

        invitationRepository.save(invitation);

        notifyInvitationSent(invitation);
    }

    @Transactional
    public void replyToInvitation(String invitationId, boolean accepted) {
        TeamInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new NoSuchElementException("Invito non trovato"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvitationAlreadyHandledException(invitation.getId());
        }

        if (accepted) {
            User receiver = invitation.getReceiver();
            Team team = invitation.getTeam();

            if (userRepository.isUserInAnyTeam(receiver.getId())) {
                throw new UserAlreadyInTeamException(receiver.getUsername());
            }

            team.getMembers().add(receiver);
            receiver.getTeams().add(team);

            invitation.setStatus(InvitationStatus.ACCEPTED);

            teamRepository.save(team);
            userRepository.save(receiver);
        } else {
            invitation.setStatus(InvitationStatus.REJECTED);
        }

        invitationRepository.save(invitation);


        notifyInvitationReplied(invitation);
    }
    public List<TeamInvitation> getAllInvitations() {
        return invitationRepository.findAll();
    }

    public TeamInvitation getInvitationById(String id) {
        return invitationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invito non trovato con ID: " + id));
    }

    public List<TeamInvitation> getPendingInvitationsForUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("Utente non trovato");
        }
        return invitationRepository.findByReceiverIdAndStatus(userId, InvitationStatus.PENDING);
    }

    @Transactional
    public void replyToInvitation(String userId, String teamId, boolean accepted) {
        TeamInvitation invitation = invitationRepository
                .findByReceiverIdAndTeamIdAndStatus(userId, teamId, InvitationStatus.PENDING)
                .orElseThrow(() -> new NoSuchElementException("Nessun invito in attesa trovato per l'utente " + userId + " nel team " + teamId));

        invitation.setStatus(accepted ? InvitationStatus.ACCEPTED : InvitationStatus.REJECTED);
        invitationRepository.save(invitation);

        if (accepted) {
            Team team = invitation.getTeam();
            User receiver = invitation.getReceiver();

            if (receiver.isMemberOfActiveTeam()) {
                throw new IllegalStateException("L'utente fa già parte di un team attivo.");
            }

            team.getMembers().add(receiver);
            receiver.getTeams().add(team);

            userRepository.save(receiver);
            teamRepository.save(team);
        }
    }
}