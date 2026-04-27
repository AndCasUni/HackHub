package it.hackhub.service;

import it.hackhub.model.domain.Evaluation;
import it.hackhub.model.domain.Submission;
import it.hackhub.model.domain.UserStaff;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.repository.EvaluationRepository;
import it.hackhub.repository.SubmissionRepository;
import it.hackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Autowired
    public EvaluationService(EvaluationRepository evaluationRepository,
                             SubmissionRepository submissionRepository,
                             UserRepository userRepository) {
        this.evaluationRepository = evaluationRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void evaluateSubmission(String judgeId, String submissionId, int score, String feedback) {
        UserStaff judge = (UserStaff) userRepository.findById(judgeId)
                .orElseThrow(() -> new NoSuchElementException("Giudice non trovato"));
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NoSuchElementException("Submission non trovata"));

        if (submission.getTeam().getRegisteredHackathon().getState() != HackathonStatus.EVALUATION)
            throw new IllegalStateException("L'Hackathon non è in fase di valutazione.");

        if (!submission.getTeam().getRegisteredHackathon().getStaff().stream().noneMatch(s -> s.getId().equals(judgeId)))
            throw new SecurityException("L'utente non è un giudice autorizzato per questo hackathon.");

        if (judge.getRoleEnum() != UserRoleEnum.JUDGE)
            throw new SecurityException("Solo un JUDGE può valutare le submission.");

        if (submission.getTeam().isDisqualified())
            throw new IllegalArgumentException("La squadra è stata squalificata e non può essere valutata.");

        Evaluation evaluation = new Evaluation();
        evaluation.setJudge(judge);
        evaluation.setSubmission(submission);
        evaluation.setScore(score);
        evaluation.setFeedback(feedback);
        evaluationRepository.save(evaluation);
    }

    @Transactional
    public void updateEvaluation(String evaluationId, String judgeId, int newScore, String newFeedback) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new NoSuchElementException("Valutazione non trovata"));

        if (!evaluation.getJudge().getId().equals(judgeId))
            throw new SecurityException("Solo il giudice che ha creato la valutazione può modificarla.");

        if (evaluation.getSubmission().getTeam().getRegisteredHackathon().getState()
                != HackathonStatus.EVALUATION)
            throw new IllegalStateException("Le valutazioni possono essere modificate solo durante la fase di valutazione.");

        evaluation.setScore(newScore);
        evaluation.setFeedback(newFeedback);
        evaluationRepository.save(evaluation);
    }

    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    public Evaluation getEvaluationById(String id) {
        return evaluationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Valutazione non trovata con ID: " + id));
    }

    public List<Evaluation> getEvaluationsByTeam(String teamId) {
        return evaluationRepository.findBySubmission_Team_Id(teamId);
    }

    public List<Evaluation> getEvaluationsByHackathon(String hackathonId) {
        return evaluationRepository.findBySubmission_Team_RegisteredHackathon_Id(hackathonId);
    }
}
