package it.hackhub.controller;

import it.hackhub.model.domain.Submission;
import it.hackhub.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    public static class SubmitRequest {
        public String id;
        public String teamId;
        public String githubUrl;
    }

    @PostMapping
    public ResponseEntity<?> submitWork(@RequestBody SubmitRequest req) {
        try {
            Submission created = submissionService.submitWork(req.teamId, req.githubUrl ,req.id);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Errore: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<Submission>> getAll() {
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(submissionService.getSubmissionById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // URL: http://localhost:8080/api/submissions/team/{teamId}
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getByTeam(@PathVariable String teamId) {
        try {
            return ResponseEntity.ok(submissionService.getSubmissionByTeam(teamId));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // GET: Mostra tutte le sottomissioni di un Hackathon
    // URL: http://localhost:8080/api/submissions/hackathon/{hackathonId}
    @GetMapping("/hackathon/{hackathonId}")
    public ResponseEntity<List<Submission>> getByHackathon(@PathVariable String hackathonId) {
        return ResponseEntity.ok(submissionService.getSubmissionsByHackathon(hackathonId));
    }
}