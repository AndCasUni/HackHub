package it.hackhub.exception;

public class UserAlreadyInTeamException extends RuntimeException {

    public UserAlreadyInTeamException(String userId) {
        super("Utente già membro di una squadra: ID=%s".formatted(userId));
    }
}
