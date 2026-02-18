package it.hackhub.state;

import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.Submission;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.HackathonStatus;

public class RegistrationState implements HackathonState {

    @Override
    public void transitionToOngoing(Hackathon hackathon) {
        // 1. Verifica presenza di almeno un Giudice
        boolean hasJudge = hackathon.getStaff().stream()
                .anyMatch(u -> u.getRoleEnum() == it.hackhub.model.enums.UserRoleEnum.JUDGE);

        // 2. Verifica presenza di almeno un Mentore
        boolean hasMentor = hackathon.getStaff().stream()
                .anyMatch(u -> u.getRoleEnum() == it.hackhub.model.enums.UserRoleEnum.MENTOR);

        if (!hasJudge || !hasMentor) {
            throw new IllegalStateException("Impossibile avviare l'hackathon: assicurarsi di aver assegnato almeno un Giudice e un Mentore.");
        }

        // 3. Controllo opzionale: presenza di almeno un team iscritto
        if (hackathon.getRegisteredTeams().isEmpty()) {
            throw new IllegalStateException("Impossibile avviare l'hackathon senza team iscritti.");
        }

        // Transizione di stato
        hackathon.setState(HackathonStatus.ONGOING);

    }

    @Override
    public void transitionToEvaluation(Hackathon hackathon) {
        throw new IllegalStateException("Impossibile passare a valutazione dalla registrazione.");
    }

    @Override
    public void transitionToCompleted(Hackathon hackathon) {
        throw new IllegalStateException("Impossibile concludere un hackathon in registrazione.");
    }

    @Override
    public boolean canRegisterTeam() { return true; }

    @Override
    public void registerTeam(Hackathon hackathon, Team team) {
        hackathon.getRegisteredTeams().add(team);
    }

    @Override
    public boolean canSubmit() { return false; }

    @Override
    public void submitWork(Hackathon hackathon, Team team, Submission submission) {
        throw new IllegalStateException("Sottomissioni non permesse in fase di registrazione.");
    }

    @Override
    public boolean canEvaluate() { return false; }

    @Override
    public void evaluateSubmission(Hackathon hackathon, Submission submission, User judge) {
        throw new IllegalStateException("Valutazioni non permesse in fase di registrazione.");
    }

    @Override
    public boolean canAssignStaff() { return true; }

    @Override
    public boolean canDeclareWinner() { return false; }

    @Override
    public void declareWinner(Hackathon hackathon) {
        throw new IllegalStateException("Impossibile dichiarare un vincitore ora.");
    }

    @Override
    public boolean canRequestSupport() { return false; }

    @Override
    public String getStateName() { return "REGISTRATION"; }

    @Override
    public void cancelHackathon(Hackathon hackathon) {
        // Verifica se ci sono team iscritti
        if (!hackathon.getRegisteredTeams().isEmpty()) {
            // Se ci sono team, la chiusura potrebbe richiedere logiche di rimborso (se previsto)
            // o semplicemente una notifica di annullamento specifica.
            System.out.println("Annullamento hackathon con team iscritti in corso...");
        }

        // L'hackathon passa direttamente a COMPLETED (o uno stato di ANNULLATO se previsto)
        hackathon.setState(HackathonStatus.COMPLETED);

    }
}