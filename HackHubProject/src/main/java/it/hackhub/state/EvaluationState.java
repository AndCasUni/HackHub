package it.hackhub.state;

import it.hackhub.model.domain.*;
import it.hackhub.model.enums.HackathonStatus;

public class EvaluationState implements HackathonState {

    @Override
    public void transitionToOngoing(Hackathon hackathon) {
        throw new IllegalStateException("Impossibile tornare alla fase in corso.");
    }

    @Override
    public void transitionToEvaluation(Hackathon hackathon) {
        throw new IllegalStateException("Già in fase di valutazione.");
    }

    @Override
    public void transitionToCompleted(Hackathon hackathon) {
        hackathon.setState(HackathonStatus.COMPLETED);
    }

    @Override
    public boolean canRegisterTeam() { return false; }

    @Override
    public void registerTeam(Hackathon hackathon, Team team) {
        throw new IllegalStateException("Hackathon terminato.");
    }

    @Override
    public boolean canSubmit() { return false; }

    @Override
    public void submitWork(Hackathon hackathon, Team team, Submission submission) {
        throw new IllegalStateException("Scadenza sottomissioni superata.");
    }

    @Override
    public boolean canEvaluate() { return true; }

    @Override
    public void evaluateSubmission(Hackathon hackathon, Submission submission, User judge) {
        // Logica di valutazione [cite: 20]
    }

    @Override
    public boolean canAssignStaff() { return false; }

    @Override
    public boolean canDeclareWinner() { return true; }

    @Override
    public void declareWinner(Hackathon hackathon) {
        // Effettua la transizione allo stato finale
        hackathon.setState(it.hackhub.model.enums.HackathonStatus.COMPLETED);

        // REQUISITO: Quando l'hackathon è concluso, lo staff deve essere liberato (Observer)
    }

    @Override
    public boolean canRequestSupport() { return false; }

    @Override
    public String getStateName() { return "EVALUATION"; }
    @Override
    public void cancelHackathon(Hackathon hackathon) {
        throw new IllegalStateException("Impossibile cancellare l'hackathon ora.");
    }
}