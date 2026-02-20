package it.hackhub.service;

import it.hackhub.exception.TeamNotInOngoingHackathonException;
import it.hackhub.exception.UserAlreadyInTeamException;
import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.repository.HackathonRepository;
import it.hackhub.repository.TeamRepository;
import it.hackhub.repository.UserRepository;

import java.util.List;

public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;

    public TeamService(TeamRepository teamRepository,
                       UserRepository userRepository,
                       HackathonRepository hackathonRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.hackathonRepository = hackathonRepository;
    }

    public Team createTeam(String name, String leaderId) {
        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Vincolo Team Unico
        if (leader.isMemberOfActiveTeam()) {
            throw new UserAlreadyInTeamException(leaderId);
        }

        Team team = new Team();
        team.setName(name);
        team.setLeader(leader);
        team.getMembers().add(leader);
       teamRepository.save(team);
       return team;
    }
    public void registerTeamToHackathon(String teamId, String hackathonId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        Hackathon hackathon = hackathonRepository.findById(hackathonId);

        if (hackathon.getState() != HackathonStatus.REGISTRATION &&
                hackathon.getState() != HackathonStatus.ONGOING) {
            throw new TeamNotInOngoingHackathonException(teamId);
        }

        team.setRegisteredHackathon(hackathon);
        teamRepository.save(team);
    }

    public List<Team> findTeamsByHackathon(String hackathonId) {
        return teamRepository.findByHackathon(hackathonId);
    }
}
