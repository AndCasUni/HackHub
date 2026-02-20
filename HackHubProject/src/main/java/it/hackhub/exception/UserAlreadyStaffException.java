package it.hackhub.exception;

public class UserAlreadyStaffException extends RuntimeException {
    public UserAlreadyStaffException(String message) {
        super(message);
    }

    public UserAlreadyStaffException(String userId, String hackathonId) {
        super("Utente già staff: user=%s, hackathon=%s"
                .formatted(userId, hackathonId));
    }
}
