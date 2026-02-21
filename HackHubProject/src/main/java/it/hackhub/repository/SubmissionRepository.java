package it.hackhub.repository;

import it.hackhub.model.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {

    // Trova sottomissione per Team
    Optional<Submission> findByTeamId(String teamId);

    // Trova tutte le sottomissioni di un Hackathon
    List<Submission> findByTeam_RegisteredHackathon_Id(String hackathonId);
}