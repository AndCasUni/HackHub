package it.hackhub.dto.request.evaluation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class UpdateEvaluationRequest {
    @NotBlank public String judgeId;
    @Min(0) @Max(100) public int score;
    public String feedback;
}