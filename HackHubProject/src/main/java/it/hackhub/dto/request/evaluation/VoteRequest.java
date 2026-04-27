package it.hackhub.dto.request.evaluation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class VoteRequest {
    @NotBlank public String judgeId;
    @NotBlank public String submissionId;
    @Min(1) @Max(10) public int score;
    public String feedback;
}