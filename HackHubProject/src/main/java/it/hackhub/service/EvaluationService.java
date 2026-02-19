package it.hackhub.service;

import it.hackhub.model.domain.*;
import it.hackhub.exception.InvalidHackathonStateException;
import it.hackhub.repository.*;
import it.hackhub.model.enums.UserRoleEnum;

public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public EvaluationService(EvaluationRepository evaluationRepository,
                             SubmissionRepository submissionRepository,
                             UserRepository userRepository) {
        this.evaluationRepository = evaluationRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    public Evaluation evaluateSubmission(String submissionId, String judgeId, int score, String feedback) {
        Submission submission = submissionRepository.findById(submissionId);
        User judge = userRepository.findById(judgeId)
                .orElseThrow(() -> new RuntimeException("Giudice non trovato"));

        Hackathon hackathon = submission.getTeam().getRegisteredHackathon();

        // 1. Vincolo: Lo stato deve permettere la valutazione (EVALUATION)
        if (!hackathon.getCurrentStateObject().canEvaluate()) {
            throw new InvalidHackathonStateException("La fase di valutazione non è attiva.");
        }

        // 2. Vincolo: L'utente deve essere un JUDGE
        if (judge.getRoleEnum() != UserRoleEnum.JUDGE) {
            throw new IllegalStateException("Solo un Giudice può valutare le sottomissioni.");
        }

        // 3. Vincolo: Il giudice deve essere assegnato a questo Hackathon
        if (!hackathon.getStaff().contains(judge)) {
            throw new IllegalStateException("Il giudice non è assegnato a questo hackathon.");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setSubmission(submission);
        evaluation.setJudge(judge);
        evaluation.setScore(score);
        evaluation.setFeedback(feedback);

        evaluationRepository.save(evaluation);
        return evaluation;
    }

    public double getAverageScore(String submissionId) {
        return evaluationRepository.getAverageScore(submissionId);
    }
}