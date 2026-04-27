package it.hackhub.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 — Risorsa non trovata
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    // 409 — Conflitto (utente già in team, già staff, ecc.)
    @ExceptionHandler(UserAlreadyInTeamException.class)
    public ResponseEntity<String> handleAlreadyInTeam(UserAlreadyInTeamException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler(UserAlreadyStaffException.class)
    public ResponseEntity<String> handleAlreadyStaff(UserAlreadyStaffException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler(StaffMemberAlreadyOccupiedException.class)
    public ResponseEntity<String> handleStaffOccupied(StaffMemberAlreadyOccupiedException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler(InvitationAlreadyHandledException.class)
    public ResponseEntity<String> handleInvitationHandled(InvitationAlreadyHandledException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }

    // 400 — Richiesta non valida
    @ExceptionHandler(InvalidHackathonStateException.class)
    public ResponseEntity<String> handleInvalidState(InvalidHackathonStateException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(TeamNotInOngoingHackathonException.class)
    public ResponseEntity<String> handleTeamNotOngoing(TeamNotInOngoingHackathonException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(SubmissionDeadlineExceededException.class)
    public ResponseEntity<String> handleDeadline(SubmissionDeadlineExceededException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArg(IllegalArgumentException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    // 401 — Non autorizzato
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurity(SecurityException e) {
        return ResponseEntity.status(401).body(e.getMessage());
    }

    // 500 — Fallback generico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception e) {
        return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
    }
}