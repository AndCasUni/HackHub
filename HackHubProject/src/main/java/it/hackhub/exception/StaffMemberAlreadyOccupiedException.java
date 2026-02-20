package it.hackhub.exception;

public class StaffMemberAlreadyOccupiedException extends RuntimeException {
    public StaffMemberAlreadyOccupiedException(String userId) {
        super("Lo staffer con ID " + userId + " è già assegnato a un altro hackathon attivo.");
    }
}