package it.hackhub.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.hackhub.dto.request.evaluation.UpdateEvaluationRequest;
import it.hackhub.dto.request.evaluation.VoteRequest;
import it.hackhub.model.domain.Evaluation;
import it.hackhub.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Valutazioni", description = "Gestione valutazioni dei giudici")
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    @Autowired private EvaluationService evaluationService;

    @PostMapping
    public ResponseEntity<String> evaluate(@Valid @RequestBody VoteRequest req) {
        evaluationService.evaluateSubmission(req.judgeId, req.submissionId, req.score, req.feedback);
        return ResponseEntity.ok("Voto registrato!");
    }

    @GetMapping
    public ResponseEntity<List<Evaluation>> getAll() {
        return ResponseEntity.ok(evaluationService.getAllEvaluations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evaluation> getById(@PathVariable String id) {
        return ResponseEntity.ok(evaluationService.getEvaluationById(id));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Evaluation>> getByTeam(@PathVariable String teamId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByTeam(teamId));
    }

    @GetMapping("/hackathon/{hackathonId}")
    public ResponseEntity<List<Evaluation>> getByHackathon(@PathVariable String hackathonId , @PathVariable String requesterId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByHackathon(hackathonId, requesterId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEvaluation(
            @PathVariable String id,
            @Valid @RequestBody UpdateEvaluationRequest req) {
        evaluationService.updateEvaluation(id, req.judgeId, req.score, req.feedback);
        return ResponseEntity.ok("Valutazione aggiornata con successo.");
    }
}