package it.hackhub.domain.state;

import it.hackhub.domain.model.Hackathon;
import it.hackhub.domain.model.SubTeam;
import it.hackhub.domain.model.Submission;

public class ClosedState implements HackathonState {

    private Hackathon hackathon;

    @Override
    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

    @Override
    public void openRegistration(Hackathon hackathon) {
        hackathon.setState(new RegistrationState());
    }
    @Override
    public void addTeam(Hackathon hackathon, SubTeam team) {
        throw new IllegalStateException("Hackathon concluso");
    }

    @Override
    public void addSubmission(Hackathon hackathon, Submission submission) {
        throw new IllegalStateException("Hackathon concluso");
    }

    @Override
    public void evaluateSubmission(Hackathon hackathon, Submission submission) {
        throw new IllegalStateException("Hackathon chiuso");
    }

    @Override
    public void transitionToRunning(Hackathon hackathon) {
        throw new IllegalStateException("Non retroattivo");
    }

    @Override
    public void transitionToEvaluation(Hackathon hackathon) {
        throw new IllegalStateException("Non retroattivo");
    }

    @Override
    public void transitionToClosed(Hackathon hackathon) {
        // Già chiuso
    }

    @Override
    public String getStatus() { return "CONCLUSO"; }
}
