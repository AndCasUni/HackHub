package it.hackhub.dto.request.hackathon;

import jakarta.validation.constraints.NotBlank;

public class ReportTeamRequest {
    @NotBlank public String mentorId;
    @NotBlank public String teamId;
    @NotBlank public String reason;
}