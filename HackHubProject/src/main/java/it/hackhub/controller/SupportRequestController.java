package it.hackhub.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.hackhub.dto.request.support.AcceptSupportRequest;
import it.hackhub.dto.request.support.CreateSupportRequestDto;
import it.hackhub.model.domain.SupportRequest;
import it.hackhub.service.SupportRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Support Requests", description = "Gestione richieste di supporto")
@RestController
@RequestMapping("/api/support")
public class SupportRequestController {

    @Autowired private SupportRequestService supportRequestService;

    @PostMapping
    public ResponseEntity<SupportRequest> create(@Valid @RequestBody CreateSupportRequestDto req) {
        return ResponseEntity.ok(
                supportRequestService.createRequest(req.leaderId, req.title, req.description));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<String> acceptRequest(@PathVariable String id,
                                                @Valid @RequestBody AcceptSupportRequest req) {
        supportRequestService.acceptRequest(id, req.mentorId, req.callTime);
        return ResponseEntity.ok("Richiesta di supporto accettata. Orario call proposto.");
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<String> close(@PathVariable String id,
                                        @RequestParam String mentorId) {
        supportRequestService.closeRequest(id, mentorId);
        return ResponseEntity.ok("Richiesta di supporto chiusa (SOLVED).");
    }

    @PostMapping("/{id}/confirm-call")
    public ResponseEntity<String> confirmCall(@PathVariable String id,
                                              @RequestParam String leaderId) {
        supportRequestService.confirmCall(id, leaderId);
        return ResponseEntity.ok("Call confermata definitivamente (ACCEPTED).");
    }

    @PostMapping("/{id}/reject-call")
    public ResponseEntity<String> rejectCall(@PathVariable String id,
                                             @RequestParam String leaderId) {
        supportRequestService.rejectCall(id, leaderId);
        return ResponseEntity.ok("Call rifiutata. La richiesta è tornata PENDING.");
    }

    @GetMapping("/hackathon/{hackathonId}")
    public ResponseEntity<List<SupportRequest>> getRequestsByHackathon(
            @PathVariable String hackathonId) {
        return ResponseEntity.ok(supportRequestService.getRequestsByHackathon(hackathonId));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<SupportRequest>> getRequestsByTeam(@PathVariable String teamId) {
        return ResponseEntity.ok(supportRequestService.getRequestsByTeam(teamId));
    }
}