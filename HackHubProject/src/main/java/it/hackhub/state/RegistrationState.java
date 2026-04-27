package it.hackhub.state;

import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.Submission;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.HackathonStatus;

public class RegistrationState implements HackathonState {

    @Override
    public void transitionToOngoing(Hackathon hackathon) {
        boolean hasJudge = hackathon.getStaff().stream()
                .anyMatch(u -> u.getRoleEnum() == it.hackhub.model.enums.UserRoleEnum.JUDGE);

        boolean hasMentor = hackathon.getStaff().stream()
                .anyMatch(u -> u.getRoleEnum() == it.hackhub.model.enums.UserRoleEnum.MENTOR);

        if (!hasJudge || !hasMentor) {
            throw new IllegalStateException("Impossibile avviare l'hackathon: assicurarsi di aver assegnato almeno un Giudice e un Mentore.");
        }

        if (hackathon.getRegisteredTeams().isEmpty()) {
            throw new IllegalStateException("Impossibile avviare l'hackathon senza team iscritti.");
        }

        /*
        CONTROLLO DATA INIZIO (opzionale, dipende da come si vuole gestire la flessibilità del sistema)
        if (hackathon.getStartDate().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Impossibile avviare l'hackathon prima della data di inizio prevista.");
        }

         */

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
        if (!hackathon.getRegisteredTeams().isEmpty()) {
            System.out.println("Annullamento hackathon con team iscritti in corso...");
        }

        hackathon.setState(HackathonStatus.COMPLETED);

    }
}