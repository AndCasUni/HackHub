package it.hackhub.state;

import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.Submission;
import it.hackhub.model.domain.User;

/**
 * Interface per il pattern State degli Hackathon.
 * Ogni stato concreto implementa i comportamenti permessi in quella fase.
 */
public interface HackathonState {

    /**
     * Transizione a ONGOING
     */
    void transitionToOngoing(Hackathon hackathon);

    /**
     * Transizione a EVALUATION
     */
    void transitionToEvaluation(Hackathon hackathon);

    /**
     * Transizione a COMPLETED (dopo dichiarazione vincitore)
     */
    void transitionToCompleted(Hackathon hackathon);

    /**
     * Verifica se un team può registrarsi
     */
    boolean canRegisterTeam();

    /**
     * Registra un team (solo in REGISTRATION)
     */
    void registerTeam(Hackathon hackathon, Team team);

    /**
     * Verifica se si può inviare submission
     */
    boolean canSubmit();

    /**
     * Invia submission (solo in ONGOING)
     */
    void submitWork(Hackathon hackathon, Team team, Submission submission);

    /**
     * Verifica se si possono valutare submission
     */
    boolean canEvaluate();

    /**
     * Valuta submission (solo in EVALUATION)
     */
    void evaluateSubmission(Hackathon hackathon, Submission submission, User judge);

    /**
     * Verifica se si può assegnare staff (Judge/Mentor)
     */
    boolean canAssignStaff();

    /**
     * Verifica se si può dichiarare vincitore
     */
    boolean canDeclareWinner();

    /**
     * Dichiara vincitore (solo in EVALUATION)
     */
    void declareWinner(Hackathon hackathon);

    /**
     * Verifica se i team possono creare richieste supporto
     */
    boolean canRequestSupport();

    /**
     * Nome dello stato corrente
     */
    String getStateName();

    void cancelHackathon(Hackathon hackathon);
}
