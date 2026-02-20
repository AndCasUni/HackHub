package it.hackhub.exception;

public class InvalidHackathonStateException extends RuntimeException {
    public InvalidHackathonStateException(String message) {
        super(message);
    }

    public InvalidHackathonStateException(String currentState, String requiredState) {
        super("Transizione non valida: attuale=%s, richiesto=%s"
                .formatted(currentState, requiredState));
    }
}
