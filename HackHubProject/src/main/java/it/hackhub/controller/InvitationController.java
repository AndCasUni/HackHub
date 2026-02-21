package it.hackhub.controller;

import it.hackhub.model.domain.TeamInvitation;
import it.hackhub.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    public static class InviteRequest {
        public String senderId;
        public String receiverId;
        public String teamId;
    }

    // 1. Invia Invito
    @PostMapping("/send")
    public ResponseEntity<String> inviteUser(@RequestBody InviteRequest req) {
        try {
            invitationService.inviteUser(req.senderId, req.receiverId, req.teamId);
            return ResponseEntity.ok("Invito inviato!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Rispondi all'invito (Accetta/Rifiuta)
    @PostMapping("/{invitationId}/reply")
    public ResponseEntity<String> reply(@PathVariable String invitationId, @RequestParam boolean accepted) {
        try {
            invitationService.replyToInvitation(invitationId, accepted);
            return ResponseEntity.ok("Risposta registrata: " + (accepted ? "ACCETTATO" : "RIFIUTATO"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<TeamInvitation>> getAll() {
        return ResponseEntity.ok(invitationService.getAllInvitations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(invitationService.getInvitationById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // URL: http://localhost:8080/api/invitations/user/{userId}/pending
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<?> getUserPendingInvitations(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(invitationService.getPendingInvitationsForUser(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Rispondi all'invito usando userId e teamId
    // URL: POST http://localhost:8080/api/invitations/reply?userId=p2&teamId=t1&accepted=true
    @PostMapping("/reply")
    public ResponseEntity<String> reply(@RequestParam String userId, @RequestParam String teamId, @RequestParam boolean accepted) {
        try {
            invitationService.replyToInvitation(userId, teamId, accepted);
            String message = accepted ? "Invito accettato, sei entrato nel team!" : "Invito rifiutato.";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}