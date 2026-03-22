package it.hackhub.service;

import it.hackhub.exception.TeamNotInOngoingHackathonException;
import it.hackhub.exception.UserAlreadyInTeamException;
import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.repository.HackathonRepository;
import it.hackhub.repository.TeamInvitationRepository;
import it.hackhub.repository.TeamRepository;
import it.hackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;
    private final NotificationService notificationService;
    private final TeamInvitationRepository teamInvitationRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository,
                       UserRepository userRepository,
                       HackathonRepository hackathonRepository,
                       NotificationService notificationService,
                       TeamInvitationRepository teamInvitationRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.hackathonRepository = hackathonRepository;
        this.notificationService = notificationService;
        this.teamInvitationRepository = teamInvitationRepository;
    }

    @Transactional
    public Team createTeam(String name, String leaderId, String customId) {

        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        if (leader.isMemberOfActiveTeam()) throw new UserAlreadyInTeamException(leaderId);
        Team team = new Team();
        if (customId != null && !customId.isBlank()) team.setId(customId);
        team.setName(name);
        team.setLeader(leader);
        team.getMembers().add(leader);
        return teamRepository.save(team);
    }

    @Transactional
    public void registerTeamToHackathon(String teamId, String hackathonId, String requesterId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team non trovato"));

        // PRECONDIZIONE : Essere Leader del Team
        if (!team.getLeader().getId().equals(requesterId)) {
            throw new SecurityException("Solo il leader può iscrivere il team all'hackathon.");
        }

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon non trovato"));

        // PRECONDIZIONE XML: Hackathon in fase di iscrizione
        if (hackathon.getState() != HackathonStatus.REGISTRATION) {
            throw new IllegalStateException("L'Hackathon non è in fase di iscrizione.");
        }

        team.setRegisteredHackathon(hackathon);
        hackathon.getRegisteredTeams().add(team);

        teamRepository.save(team);

        String message = "Il tuo team '" + team.getName() + "' è stato iscritto all'hackathon '" + hackathon.getName() + "'.";
        for (User member : team.getMembers()) {
            notificationService.sendNotification(member, "Iscrizione Hackathon", message, "REGISTRATION_CONFIRMED");
        }
    }

    // DISISCRIZIONE TEAM (Solo Leader, Solo se REGISTRATION)
    @Transactional
    public void unregisterTeamFromHackathon(String teamId, String leaderId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team non trovato"));

        if (!team.getLeader().getId().equals(leaderId)) {
            throw new SecurityException("Solo il leader può disiscrivere il team.");
        }

        Hackathon hackathon = team.getRegisteredHackathon();
        if (hackathon == null) {
            throw new IllegalStateException("Il team non è iscritto a nessun hackathon.");
        }

        if (hackathon.getState() != HackathonStatus.REGISTRATION) {
            throw new IllegalStateException("Impossibile disiscriversi: l'hackathon non è più in fase di registrazione.");
        }

        team.setRegisteredHackathon(null);
        hackathon.getRegisteredTeams().remove(team);

        teamRepository.save(team);

        for (User member : team.getMembers()) {
            notificationService.sendNotification(member, "Disiscrizione Hackathon", "Il team si è ritirato dall'hackathon.", "UNREGISTRATION");
        }
    }

    // ELIMINA TEAM (Solo Leader)
    @Transactional
    public void deleteTeam(String teamId, String leaderId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team non trovato"));

        if (!team.getLeader().getId().equals(leaderId)) {
            throw new SecurityException("Solo il leader può eliminare il team.");
        }

        if (team.getRegisteredHackathon() != null &&
                team.getRegisteredHackathon().getState() == HackathonStatus.ONGOING) {
            throw new IllegalStateException("Non puoi eliminare il team mentre l'hackathon è in corso.");
        }

        for (User member : team.getMembers()) {
            member.getTeams().remove(team);
        }
        team.getMembers().clear();

        teamInvitationRepository.deleteByTeamId(teamId);
        teamRepository.delete(team);
    }


    @Transactional
    public void leaveTeam(String teamId, String userId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NoSuchElementException("Team non trovato"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Utente non trovato"));

        if (!team.getMembers().contains(user)) throw new IllegalArgumentException("L'utente non fa parte di questo team.");

        team.getMembers().remove(user);
        user.getTeams().remove(team);

        if (team.getLeader().getId().equals(userId)) {
            if (team.getMembers().isEmpty()) {
                teamInvitationRepository.deleteByTeamId(teamId);
                teamRepository.delete(team);
                return;
            } else {
                User newLeader = team.getMembers().get(new Random().nextInt(team.getMembers().size()));
                team.setLeader(newLeader);
            }
        }
        teamRepository.save(team);
        userRepository.save(user);
    }

    @Transactional
    public void changeLeader(String teamId, String currentLeaderId, String newLeaderId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NoSuchElementException("Team non trovato"));
        if (!team.getLeader().getId().equals(currentLeaderId)) throw new SecurityException("Solo l'attuale leader può cedere il ruolo.");
        User newLeader = team.getMembers().stream().filter(m -> m.getId().equals(newLeaderId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Il nuovo leader deve essere un membro del team."));
        team.setLeader(newLeader);
        teamRepository.save(team);
    }

    public List<Team> getAllTeams() { return teamRepository.findAll(); }
    public Team getTeamById(String id) { return teamRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Team non trovato")); }
}