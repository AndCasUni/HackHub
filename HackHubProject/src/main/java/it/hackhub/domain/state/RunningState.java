package it.hackhub.domain.state;

import it.hackhub.domain.model.Hackathon;
import it.hackhub.domain.model.SubTeam;
import it.hackhub.domain.model.Submission;

public class RunningState implements HackathonState {

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
        throw new IllegalStateException("Iscrizioni chiuse");
    }

    @Override
    public void addSubmission(Hackathon hackathon, Submission submission) {
        hackathon.getSubmissions().add(submission);
    }

    @Override
    public void transitionToRunning(Hackathon hackathon) {
        // Già in corso
    }

    @Override
    public void transitionToEvaluation(Hackathon hackathon) {
        hackathon.setState(new EvaluationState());
    }

    @Override
    public void transitionToClosed(Hackathon hackathon) {
        throw new IllegalStateException("Prima valutazione");
    }

    @Override
    public void evaluateSubmission(Hackathon hackathon, Submission submission) {
        throw new IllegalStateException("Hackathon in corso");
    }

    @Override
    public String getStatus() { return "IN CORSO"; }
}
