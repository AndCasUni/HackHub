package it.hackhub.controller;

import it.hackhub.model.domain.SupportRequest;
import it.hackhub.service.SupportRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportRequestController {

    @Autowired
    private SupportRequestService supportRequestService;

    public static class CreateRequestDTO {
        public String leaderId;
        public String title;
        public String description;
    }

    public static class AcceptSupportRequest {
        public String mentorId;
        public java.time.LocalDateTime callTime;
    }

    // INVIA RICHIESTA
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRequestDTO req) {
        try {
            SupportRequest created = supportRequestService.createRequest(req.leaderId, req.title, req.description);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST http://localhost:8080/api/support/{requestId}/accept
    @PostMapping("/{id}/accept")
    public ResponseEntity<String> acceptRequest(
            @PathVariable String id,
            @RequestBody AcceptSupportRequest req) {
        try {
            supportRequestService.acceptRequest(id, req.mentorId, req.callTime);
            return ResponseEntity.ok("Richiesta di supporto accettata. Orario call proposto.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // CHIUDI RICHIESTA
    // URL: POST /api/support/{id}/close?mentorId=...
    @PostMapping("/{id}/close")
    public ResponseEntity<String> close(@PathVariable String id, @RequestParam String mentorId) {
        try {
            supportRequestService.closeRequest(id, mentorId);
            return ResponseEntity.ok("Richiesta di supporto chiusa (SOLVED).");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // LEADER ACCETTA CALL
    // URL: POST /api/support/{id}/confirm-call?leaderId=...
    @PostMapping("/{id}/confirm-call")
    public ResponseEntity<String> confirmCall(@PathVariable String id, @RequestParam String leaderId) {
        try {
            supportRequestService.confirmCall(id, leaderId);
            return ResponseEntity.ok("Call confermata definitivamente (ACCEPTED).");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // LEADER RIFIUTA CALL
    // URL: POST /api/support/{id}/reject-call?leaderId=...
    @PostMapping("/{id}/reject-call")
    public ResponseEntity<String> rejectCall(@PathVariable String id, @RequestParam String leaderId) {
        try {
            supportRequestService.rejectCall(id, leaderId);
            return ResponseEntity.ok("Call rifiutata. La richiesta è tornata PENDING.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET: Mostra tutte le richieste di supporto di un determinato Hackathon
    // URL: http://localhost:8080/api/support/hackathon/{hackathonId}
    @GetMapping("/hackathon/{hackathonId}")
    public ResponseEntity<List<SupportRequest>> getRequestsByHackathon(@PathVariable String hackathonId) {
        List<SupportRequest> requests = supportRequestService.getRequestsByHackathon(hackathonId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<SupportRequest>> getRequestsByTeam(@PathVariable String teamId) {
        List<SupportRequest> requests = supportRequestService.getRequestsByTeam(teamId);
        return ResponseEntity.ok(requests);
    }
}