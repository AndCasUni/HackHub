package it.hackhub.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.hackhub.dto.request.submission.SubmitRequest;
import it.hackhub.model.domain.Submission;
import it.hackhub.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Submission", description = "Gestione submission dei team")
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired private SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<Submission> submit(@Valid @RequestBody SubmitRequest request) {
        Submission submission = submissionService.submitWork(
                request.teamId,
                request.githubUrl,
                request.id,
                request.submitterId
        );
        return ResponseEntity.ok(submission);
    }

    @GetMapping
    public ResponseEntity<List<Submission>> getAll() {
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Submission> getById(@PathVariable String id) {
        return ResponseEntity.ok(submissionService.getSubmissionById(id));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<Submission> getByTeam(@PathVariable String teamId) {
        return ResponseEntity.ok(submissionService.getSubmissionByTeam(teamId));
    }

    @GetMapping("/hackathon/{hackathonId}")
    public ResponseEntity<List<Submission>> getByHackathon(
            @PathVariable String hackathonId,
            @RequestParam String requesterId) {
        return ResponseEntity.ok(
                submissionService.getSubmissionsByHackathon(hackathonId, requesterId));
    }
}