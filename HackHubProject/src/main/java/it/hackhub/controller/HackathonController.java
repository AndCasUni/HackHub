package it.hackhub.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.hackhub.dto.request.hackathon.*;
import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.service.HackathonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Hackathon", description = "Gestione hackathon")
@RestController
@RequestMapping("/api/hackathons")
public class HackathonController {

    @Autowired
    private HackathonService hackathonService;

    @PostMapping
    public ResponseEntity<Hackathon> createHackathon(@Valid @RequestBody CreateHackathonRequest req) {
        return ResponseEntity.ok(hackathonService.createHackathon(
                req.id, req.name, req.description,
                req.startDate, req.endDate,
                req.prizeAmount, req.organizerId, req.maxParticipants));
    }

    @GetMapping
    public ResponseEntity<List<Hackathon>> getAll() {
        return ResponseEntity.ok(hackathonService.getAllHackathons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hackathon> getById(@PathVariable String id) {
        return ResponseEntity.ok(hackathonService.getHackathonById(id));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<String> startHackathon(@PathVariable String id,
                                                 @Valid @RequestBody OrganizerRequest req) {
        hackathonService.startHackathon(id, req.organizerId);
        return ResponseEntity.ok("Hackathon avviato con successo!");
    }

    @PostMapping("/{id}/staff")
    public ResponseEntity<String> addStaff(@PathVariable String id, @PathVariable String requesterId,
                                           @Valid @RequestBody AddStaffRequest req) {
        hackathonService.addStaff(id, requesterId, req.userId);
        return ResponseEntity.ok("Membro dello staff aggiunto con successo.");
    }

    @PutMapping("/{id}/evaluation")
    public ResponseEntity<String> forceEvaluation(@PathVariable String id,
                                                  @Valid @RequestBody OrganizerRequest req) {
        hackathonService.forceStateToEvaluation(id, req.organizerId);
        return ResponseEntity.ok("Hackathon forzato in stato EVALUATION!");
    }

    @PostMapping("/{id}/winner")
    public ResponseEntity<String> declareWinner(@PathVariable String id,
                                                @Valid @RequestBody OrganizerRequest req) {
        hackathonService.declareWinner(id, req.organizerId);
        return ResponseEntity.ok("Vincitore dichiarato e hackathon concluso!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHackathon(@PathVariable String id,
                                                  @RequestParam String organizerId) {
        hackathonService.deleteHackathon(id, organizerId);
        return ResponseEntity.ok("Hackathon cancellato con successo.");
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Hackathon>> getByState(@PathVariable HackathonStatus status) {
        return ResponseEntity.ok(hackathonService.getHackathonsByState(status));
    }

    @PostMapping("/report-team")
    public ResponseEntity<String> reportTeam(@Valid @RequestBody ReportTeamRequest req) {
        hackathonService.reportTeamViolation(req.mentorId, req.teamId, req.reason);
        return ResponseEntity.ok("Team segnalato con successo.");
    }

    @PostMapping("/disqualify-team")
    public ResponseEntity<String> disqualifyTeam(@Valid @RequestBody DisqualifyTeamRequest req) {
        hackathonService.disqualifyTeam(req.organizerId, req.teamId);
        return ResponseEntity.ok("Team squalificato con successo.");
    }
}