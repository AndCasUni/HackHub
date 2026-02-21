package it.hackhub.service;

import it.hackhub.exception.SubmissionDeadlineExceededException;
import it.hackhub.exception.TeamNotInOngoingHackathonException;
import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.Submission;
import it.hackhub.model.domain.Team;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.repository.SubmissionRepository;
import it.hackhub.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository, TeamRepository teamRepository) {
        this.submissionRepository = submissionRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public Submission submitWork(String teamId, String githubUrl, String customId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team non trovato"));

        Hackathon hackathon = team.getRegisteredHackathon();

        // Controlli validità
        if (hackathon == null || hackathon.getState() != HackathonStatus.ONGOING) {
            throw new TeamNotInOngoingHackathonException(team.getName());
        }

        if (LocalDateTime.now().isAfter(hackathon.getEndDate())) {
            throw new SubmissionDeadlineExceededException(hackathon.getEndDate().toString());
        }

        // Verifica se esiste già una sottomissione (update o create)
        Submission submission = submissionRepository.findByTeamId(teamId).orElse(new Submission());

        if (submission.getId() == null || !submissionRepository.existsById(submission.getId())) {
            if (customId != null && !customId.isBlank()) {
                submission.setId(customId);
            }
        }

        if (submission.getTeam() == null) {
            submission.setTeam(team);
        }

        submission.setTeam(team);
        submission.setGithubUrl(githubUrl);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepository.save(submission);
    }
    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public Submission getSubmissionById(String id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Submission non trovata con ID: " + id));
    }

    public Submission getSubmissionByTeam(String teamId) {
        return submissionRepository.findByTeamId(teamId)
                .orElseThrow(() -> new NoSuchElementException("Nessuna sottomissione trovata per questo team"));
    }

    public List<Submission> getSubmissionsByHackathon(String hackathonId) {
        return submissionRepository.findByTeam_RegisteredHackathon_Id(hackathonId);
    }
}