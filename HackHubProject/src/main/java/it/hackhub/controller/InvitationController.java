package it.hackhub.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.hackhub.dto.request.invitation.InviteRequest;
import it.hackhub.model.domain.TeamInvitation;
import it.hackhub.service.InvitationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inviti", description = "Gestione inviti nei team")
@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    @Autowired private InvitationService invitationService;

    @PostMapping("/send")
    public ResponseEntity<String> inviteUser(@Valid @RequestBody InviteRequest req) {
        invitationService.inviteUser(req.senderId, req.receiverId, req.teamId);
        return ResponseEntity.ok("Invito inviato con successo!");
    }

    @PostMapping("/{invitationId}/reply")
    public ResponseEntity<String> reply(@PathVariable String invitationId,
                                        @RequestParam boolean accepted) {
        invitationService.replyToInvitation(invitationId, accepted);
        return ResponseEntity.ok("Risposta registrata: " + (accepted ? "ACCETTATO" : "RIFIUTATO"));
    }

    @GetMapping
    public ResponseEntity<List<TeamInvitation>> getAll() {
        return ResponseEntity.ok(invitationService.getAllInvitations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamInvitation> getById(@PathVariable String id) {
        return ResponseEntity.ok(invitationService.getInvitationById(id));
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<TeamInvitation>> getPendingForUser(@PathVariable String userId) {
        return ResponseEntity.ok(invitationService.getPendingInvitationsForUser(userId));
    }
}