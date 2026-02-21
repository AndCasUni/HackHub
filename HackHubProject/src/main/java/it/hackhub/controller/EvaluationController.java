package it.hackhub.controller;

import it.hackhub.model.domain.Evaluation;
import it.hackhub.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    public static class VoteRequest {
        public String judgeId;
        public String submissionId;
        public int score;
        public String feedback;
    }

    @PostMapping
    public ResponseEntity<String> evaluate(@RequestBody VoteRequest req) {
        try {
            evaluationService.evaluateSubmission(req.judgeId, req.submissionId, req.score, req.feedback);
            return ResponseEntity.ok("Voto registrato!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Errore: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<Evaluation>> getAll() {
        return ResponseEntity.ok(evaluationService.getAllEvaluations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(evaluationService.getEvaluationById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    // GET: Valutazioni ricevute da un Team
    // URL: http://localhost:8080/api/evaluations/team/{teamId}
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Evaluation>> getByTeam(@PathVariable String teamId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByTeam(teamId));
    }

    // GET: Tutte le valutazioni di un Hackathon
    // URL: http://localhost:8080/api/evaluations/hackathon/{hackathonId}
    @GetMapping("/hackathon/{hackathonId}")
    public ResponseEntity<List<Evaluation>> getByHackathon(@PathVariable String hackathonId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByHackathon(hackathonId));
    }

}