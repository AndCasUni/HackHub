package it.hackhub.exception;

public class TeamNotInOngoingHackathonException extends RuntimeException {

    public TeamNotInOngoingHackathonException(String teamId) {
        super("Team non registrato a hackathon in corso: ID=%s".formatted(teamId));
    }
}
