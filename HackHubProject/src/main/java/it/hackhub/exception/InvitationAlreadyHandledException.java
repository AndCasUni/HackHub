package it.hackhub.exception;

public class InvitationAlreadyHandledException extends RuntimeException {

    public InvitationAlreadyHandledException(String invitationId) {
        super("Invito già gestito: ID=%s".formatted(invitationId));
    }
}
