package it.hackhub.state;

import it.hackhub.model.domain.*;
import it.hackhub.model.enums.HackathonStatus;

public class OngoingState implements HackathonState {

    @Override
    public void transitionToOngoing(Hackathon hackathon) {
        throw new IllegalStateException("L'hackathon è già in corso.");
    }

    @Override
    public void transitionToEvaluation(Hackathon hackathon) {
        hackathon.setState(HackathonStatus.EVALUATION);
    }

    @Override
    public void transitionToCompleted(Hackathon hackathon) {
        throw new IllegalStateException("Deve prima passare per la fase di valutazione.");
    }

    @Override
    public boolean canRegisterTeam() { return false; }

    @Override
    public void registerTeam(Hackathon hackathon, Team team) {
        throw new IllegalStateException("Iscrizioni chiuse.");
    }

    @Override
    public boolean canSubmit() { return true; }

    @Override
    public void submitWork(Hackathon hackathon, Team team, Submission submission) {
        // Logica per salvare o aggiornare la submission
        submission.setSubmittedAt(java.time.LocalDateTime.now());
    }

    @Override
    public boolean canEvaluate() { return false; }

    @Override
    public void evaluateSubmission(Hackathon hackathon, Submission submission, User judge) {
        throw new IllegalStateException("La valutazione inizia al termine dell'evento.");
    }

    @Override
    public boolean canAssignStaff() { return true; }

    @Override
    public boolean canDeclareWinner() { return false; }

    @Override
    public void declareWinner(Hackathon hackathon) {
        throw new IllegalStateException("L'evento è ancora in corso.");
    }

    @Override
    public boolean canRequestSupport() { return true; }

    @Override
    public String getStateName() { return "ONGOING"; }

    @Override
    public void cancelHackathon(Hackathon hackathon) {
        throw new IllegalStateException("Impossibile cancellare l'hackathon ora.");
    }
}