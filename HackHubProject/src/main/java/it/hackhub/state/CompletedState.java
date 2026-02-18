package it.hackhub.state;

import it.hackhub.model.domain.*;

public class CompletedState implements HackathonState {

    @Override public void transitionToOngoing(Hackathon h) { throw new IllegalStateException("Già concluso."); }
    @Override public void transitionToEvaluation(Hackathon h) { throw new IllegalStateException("Già concluso."); }
    @Override public void transitionToCompleted(Hackathon h) { throw new IllegalStateException("Già concluso."); }

    @Override public boolean canRegisterTeam() { return false; }
    @Override public void registerTeam(Hackathon h, Team t) {}
    @Override public boolean canSubmit() { return false; }
    @Override public void submitWork(Hackathon h, Team t, Submission s) {}
    @Override public boolean canEvaluate() { return false; }
    @Override public void evaluateSubmission(Hackathon h, Submission s, User j) {}
    @Override public boolean canAssignStaff() { return false; }
    @Override public boolean canDeclareWinner() { return false; }
    @Override public void declareWinner(Hackathon h) {}
    @Override public boolean canRequestSupport() { return false; }

    @Override
    public String getStateName() { return "COMPLETED"; }
    @Override
    public void cancelHackathon(Hackathon hackathon) {
        throw new IllegalStateException("Impossibile cancellare l'hackathon ora.");
    }
}