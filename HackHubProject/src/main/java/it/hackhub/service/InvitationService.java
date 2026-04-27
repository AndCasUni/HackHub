package it.hackhub.service;

import it.hackhub.exception.InvitationAlreadyHandledException;
import it.hackhub.exception.UserAlreadyInTeamException;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.TeamInvitation;
import it.hackhub.model.domain.UserPlayer;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.model.enums.InvitationStatus;
import it.hackhub.observer.InvitationSubject;
import it.hackhub.observer.NotificationObserver;
import it.hackhub.repository.TeamInvitationRepository;
import it.hackhub.repository.TeamRepository;
import it.hackhub.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class InvitationService extends InvitationSubject {

    private final TeamInvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final NotificationService notificationService;

    @Autowired
    public InvitationService(TeamInvitationRepository invitationRepository,
                             UserRepository userRepository,
                             TeamRepository teamRepository,
                             NotificationService notificationService) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void init() {
        this.addObserver(new NotificationObserver(notificationService));
    }

    @Transactional
    public void inviteUser(String senderId, String receiverId, String teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team non trovato"));

        UserPlayer sender = userRepository.findById(senderId)
                .filter(u -> u instanceof UserPlayer)
                .map(u -> (UserPlayer) u)
                .orElseThrow(() -> new IllegalArgumentException("Mittente non trovato o non è un PLAYER."));

        UserPlayer receiver = userRepository.findById(receiverId)
                .filter(u -> u instanceof UserPlayer)
                .map(u -> (UserPlayer) u)
                .orElseThrow(() -> new IllegalArgumentException("Destinatario non trovato o non è un PLAYER."));

        if (team.getLeader() == null || !team.getLeader().getId().equals(senderId))
            throw new SecurityException("Azione non consentita: solo il leader del team può inviare inviti.");

        //Blocco se il team è iscritto a un hackathon
        if (team.getRegisteredHackathon() != null) {
            HackathonStatus stato = team.getRegisteredHackathon().getState();
            if (stato == HackathonStatus.REGISTRATION
                    || stato == HackathonStatus.ONGOING
                    || stato == HackathonStatus.EVALUATION)
                throw new IllegalStateException(
                        "Il team è iscritto a un hackathon attivo: non è possibile invitare nuovi membri fino al termine della competizione.");
        }

        if (receiver.isMemberOfActiveTeam())
            throw new UserAlreadyInTeamException(receiver.getUsername());

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

        if (invitation.getStatus() != InvitationStatus.PENDING)
            throw new InvitationAlreadyHandledException(invitation.getId());

        if (accepted) {
            UserPlayer receiver = (UserPlayer) invitation.getReceiver();
            Team team = invitation.getTeam();

            // Blocco se il team è entrato in un hackathon attivo dopo che l'invito era stato mandato
            if (team.getRegisteredHackathon() != null) {
                HackathonStatus stato = team.getRegisteredHackathon().getState();
                if (stato == HackathonStatus.REGISTRATION
                        || stato == HackathonStatus.ONGOING
                        || stato == HackathonStatus.EVALUATION)
                    throw new IllegalStateException(
                            "Non è possibile entrare in un team già iscritto a una competizione attiva.");
            }

            if (receiver.isMemberOfActiveTeam())
                throw new UserAlreadyInTeamException(receiver.getUsername());

            team.getMembers().add(receiver);
            receiver.setCurrentTeam(team);
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
        if (!userRepository.existsById(userId))
            throw new NoSuchElementException("Utente non trovato");
        return invitationRepository.findByReceiverIdAndStatus(userId, InvitationStatus.PENDING);
    }
}