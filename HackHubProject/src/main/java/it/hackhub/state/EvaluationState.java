package it.hackhub.domain.state;

import it.hackhub.domain.model.Hackathon;
import it.hackhub.domain.model.SubTeam;
import it.hackhub.domain.model.Submission;
import it.hackhub.domain.model.Evaluation;


public class EvaluationState implements HackathonState {

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
        throw new IllegalStateException("Valutazioni in corso");
    }

    @Override
    public void addSubmission(Hackathon hackathon, Submission submission) {
        throw new IllegalStateException("Scadenza sottomissioni");
    }

    @Override
    public void evaluateSubmission(Hackathon hackathon, Submission submission) {
        // Judge può valutare
        //hackathon.getJudge().getEvaluations().add(new Evaluation());
    }

    @Override
    public void transitionToEvaluation(Hackathon hackathon) {
        // Già in valutazione
    }

    @Override
    public void transitionToClosed(Hackathon hackathon) {
        hackathon.setState(new ClosedState());
    }

    @Override
    public void transitionToRunning(Hackathon hackathon) {
        throw new IllegalStateException("Già valutato");
    }

    @Override
    public String getStatus() { return "IN VALUTAZIONE";


    }


}
