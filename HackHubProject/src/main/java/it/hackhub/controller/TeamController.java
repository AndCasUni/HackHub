package it.hackhub.controller;

import it.hackhub.model.domain.Team;
import it.hackhub.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class    TeamController {

    @Autowired
    private TeamService teamService;

    public static class CreateTeamRequest {
        public String id;
        public String name;
        public String leaderId;
    }

    // Crea Team
    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody CreateTeamRequest req) {
        Team team = teamService.createTeam(req.name, req.leaderId, req.id);
        return ResponseEntity.ok(team);
    }

    @PostMapping("/{teamId}/register/{hackathonId}")
    public ResponseEntity<String> registerToHackathon(
            @PathVariable String teamId,
            @PathVariable String hackathonId,
            @RequestParam String requesterId) {
        try {
            teamService.registerTeamToHackathon(teamId, hackathonId, requesterId);
            return ResponseEntity.ok("Team registrato con successo! Notifiche inviate ai membri.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Disiscrivi Team Hackathon
    // URL: POST /api/teams/{teamId}/unregister?leaderId=...
    @PostMapping("/{teamId}/unregister")
    public ResponseEntity<String> unregisterFromHackathon(@PathVariable String teamId, @RequestParam String leaderId) {
        try {
            teamService.unregisterTeamFromHackathon(teamId, leaderId);
            return ResponseEntity.ok("Team disiscritto dall'hackathon con successo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. Elimina Team
    // URL: DELETE /api/teams/{teamId}?leaderId=...
    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> deleteTeam(@PathVariable String teamId, @RequestParam String leaderId) {
        try {
            teamService.deleteTeam(teamId, leaderId);
            return ResponseEntity.ok("Team eliminato con successo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/{teamId}/leave")
    public ResponseEntity<String> leaveTeam(@PathVariable String teamId, @RequestParam String userId) {
        try {
            teamService.leaveTeam(teamId, userId);
            return ResponseEntity.ok("Utente rimosso dal team.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{teamId}/change-leader")
    public ResponseEntity<String> changeLeader(@PathVariable String teamId, @RequestParam String currentLeaderId, @RequestParam String newLeaderId) {
        try {
            teamService.changeLeader(teamId, currentLeaderId, newLeaderId);
            return ResponseEntity.ok("Ruolo di leader trasferito con successo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Team>> getAll() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(teamService.getTeamById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}