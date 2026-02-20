package it.hackhub.exception;

public class SubmissionDeadlineExceededException extends RuntimeException {
    public SubmissionDeadlineExceededException(String hackathonId) {
        super("Il termine per le sottomissioni per l'hackathon " + hackathonId + " è scaduto.");
    }
}