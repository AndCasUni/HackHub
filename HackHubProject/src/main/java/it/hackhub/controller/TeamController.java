package it.hackhub.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.hackhub.dto.request.team.*;
import it.hackhub.model.domain.Team;
import it.hackhub.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Team", description = "Gestione team")
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired private TeamService teamService;

    @PostMapping
    public ResponseEntity<Team> createTeam(@Valid @RequestBody CreateTeamRequest req) {
        return ResponseEntity.ok(teamService.createTeam(req.name, req.leaderId, req.id));
    }

    @GetMapping
    public ResponseEntity<List<Team>> getAll() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getById(@PathVariable String id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<String> registerToHackathon(@PathVariable String id,
                                                      @Valid @RequestBody RegisterHackathonRequest req) {
        teamService.registerTeamToHackathon(id, req.hackathonId, req.requesterId);
        return ResponseEntity.ok("Team iscritto all'hackathon con successo.");
    }

    @PostMapping("/{id}/unregister")
    public ResponseEntity<String> unregisterFromHackathon(@PathVariable String id,
                                                          @Valid @RequestBody UnregisterRequest req) {
        teamService.unregisterTeamFromHackathon(id, req.leaderId);
        return ResponseEntity.ok("Team disiscritto dall'hackathon con successo.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeam(@PathVariable String id,
                                             @Valid @RequestBody DeleteTeamRequest req) {
        teamService.deleteTeam(id, req.leaderId);
        return ResponseEntity.ok("Team eliminato con successo.");
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<String> leaveTeam(@PathVariable String id,
                                            @Valid @RequestBody LeaveTeamRequest req) {
        teamService.leaveTeam(id, req.userId);
        return ResponseEntity.ok("Utente uscito dal team con successo.");
    }

    @PutMapping("/{id}/leader")
    public ResponseEntity<String> changeLeader(@PathVariable String id,
                                               @Valid @RequestBody ChangeLeaderRequest req) {
        teamService.changeLeader(id, req.currentLeaderId, req.newLeaderId);
        return ResponseEntity.ok("Leader cambiato con successo.");
    }

    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<String> kickMember(
            @PathVariable String id,
            @PathVariable String memberId,
            @Valid @RequestBody KickMemberRequest req) {
        teamService.kickMember(id, req.leaderId, memberId);
        return ResponseEntity.ok("Membro espulso con successo.");
    }
}