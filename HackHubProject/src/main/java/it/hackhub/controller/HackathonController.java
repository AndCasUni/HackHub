package it.hackhub.controller;

import it.hackhub.model.domain.Hackathon;
import it.hackhub.service.HackathonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/hackathons")
public class HackathonController {

    @Autowired
    private HackathonService hackathonService;

    public static class CreateHackathonRequest {
        public String id;
        public String name;
        public String description;
        public LocalDateTime startDate;
        public LocalDateTime endDate;
        public Double prizeAmount;
        public String organizerId;
    }

    // POST: Creazione Hackathon con Organizzatore
    @PostMapping
    public ResponseEntity<?> createHackathon(@RequestBody CreateHackathonRequest req) {
        try {
            Hackathon created = hackathonService.createHackathon(
                    req.id,
                    req.name,
                    req.description,
                    req.startDate,
                    req.endDate,
                    req.prizeAmount,
                    req.organizerId
            );
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Errore nella creazione: " + e.getMessage());
        }
    }

    // GET: Lista di tutti gli hackathon
    @GetMapping
    public ResponseEntity<List<Hackathon>> getAll() {
        return ResponseEntity.ok(hackathonService.getAllHackathons());
    }

    // GET: Hackathon per ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(hackathonService.getHackathonById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // POST: Avviare l'hackathon
    @PostMapping("/{id}/start")
    public ResponseEntity<String> startHackathon(@PathVariable String id) {
        try {
            hackathonService.startHackathon(id);
            return ResponseEntity.ok("Hackathon avviato con successo!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Assegna Staff
    @PostMapping("/{id}/staff")
    public ResponseEntity<String> addStaff(@PathVariable String id, @RequestParam String userId) {
        try {
            hackathonService.addStaff(id, userId);
            return ResponseEntity.ok("Staff assegnato!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT: Forza stato Evaluation (TRUCCO DEMO)
    @PutMapping("/{id}/evaluation")
    public ResponseEntity<String> forceEvaluation(@PathVariable String id) {
        try {
            hackathonService.forceStateToEvaluation(id);
            return ResponseEntity.ok("Hackathon forzato in stato EVALUATION!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Dichiara Vincitore
    @PostMapping("/{id}/winner")
    public ResponseEntity<String> declareWinner(@PathVariable String id) {
        try {
            hackathonService.declareWinner(id);
            return ResponseEntity.ok("Vincitore dichiarato e hackathon concluso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // DELETE: Cancella Hackathon
    // URL: DELETE http://localhost:8080/api/hackathons/{id}?organizerId=...
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHackathon(@PathVariable String id, @RequestParam String organizerId) {
        try {
            hackathonService.deleteHackathon(id, organizerId);
            return ResponseEntity.ok("Hackathon cancellato con successo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET: Filtra per Stato
    // URL: GET http://localhost:8080/api/hackathons/status/ONGOING (oppure REGISTRATION, EVALUATION, COMPLETED)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Hackathon>> getByState(@PathVariable it.hackhub.model.enums.HackathonStatus status) {
        return ResponseEntity.ok(hackathonService.getHackathonsByState(status));
    }

    // POST: Segnala Violazione (Mentore)
    // URL: POST http://localhost:8080/api/hackathons/report-team
    @PostMapping("/report-team")
    public ResponseEntity<String> reportTeam(@RequestParam String mentorId, @RequestParam String teamId, @RequestParam String reason) {
        try {
            hackathonService.reportTeamViolation(mentorId, teamId, reason);
            return ResponseEntity.ok("Segnalazione inviata con successo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Squalifica Team (Organizer)
    // URL: POST http://localhost:8080/api/hackathons/disqualify-team
    @PostMapping("/disqualify-team")
    public ResponseEntity<String> disqualifyTeam(@RequestParam String organizerId, @RequestParam String teamId) {
        try {
            hackathonService.disqualifyTeam(organizerId, teamId);
            return ResponseEntity.ok("Team squalificato ufficialmente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}