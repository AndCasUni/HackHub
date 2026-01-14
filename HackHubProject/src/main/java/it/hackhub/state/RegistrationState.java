package it.hackhub.domain.state;

import it.hackhub.domain.model.Hackathon;
import it.hackhub.domain.model.SubTeam;
import it.hackhub.domain.model.Submission;

import java.time.LocalDateTime;

public class RegistrationState implements HackathonState {

    private Hackathon hackathon;

    @Override
    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

    @Override
    public void openRegistration(Hackathon hackathon) {
        // Già registrazione
    }
    @Override
    public void addTeam(Hackathon hackathon, SubTeam team) {
        if (LocalDateTime.now().isAfter(hackathon.getRegistrationDeadline())) {
            throw new IllegalStateException("Iscrizioni chiuse");
        }
        if (!team.hasValidSize(hackathon.getMaxTeamSize())) {
            throw new IllegalArgumentException("Sottoteam non rispetta dimensione");
        }
        hackathon.getTeams().add(team);
        System.out.println("Team iscritto: " + team.getCaptain().getUsername());
    }

    @Override
    public void addSubmission(Hackathon hackathon, Submission submission) {
        throw new IllegalStateException("Hackathon non ancora iniziato");
    }

    @Override
    public void transitionToRunning(Hackathon hackathon) {
        hackathon.setState(new RunningState());
    }

    @Override
    public void transitionToEvaluation(Hackathon hackathon) {
        throw new IllegalStateException("Passa prima per RUNNING");
    }

    @Override
    public void transitionToClosed(Hackathon hackathon) {
        throw new IllegalStateException("Passa prima per EVALUATION");
    }

    @Override
    public void evaluateSubmission(Hackathon hackathon, Submission submission) {
        throw new IllegalStateException("Valutazioni non aperte");
    }

    @Override
    public String getStatus() { return "IN ISCRIZIONE"; }
}
