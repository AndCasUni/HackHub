package it.hackhub.dto.request.hackathon;

import jakarta.validation.constraints.NotBlank;

public class DisqualifyTeamRequest {
    @NotBlank public String organizerId;
    @NotBlank public String teamId;
}