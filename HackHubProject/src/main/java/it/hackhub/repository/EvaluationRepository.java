package it.hackhub.repository;

import it.hackhub.model.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, String> {

    List<Evaluation> findByJudgeId(String judgeId);

    // Trova valutazioni per Team (passando dalla submission)
    List<Evaluation> findBySubmission_Team_Id(String teamId);

    // Trova valutazioni per Hackathon
    List<Evaluation> findBySubmission_Team_RegisteredHackathon_Id(String hackathonId);
}