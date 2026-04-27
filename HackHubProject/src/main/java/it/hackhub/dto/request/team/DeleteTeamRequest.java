package it.hackhub.dto.request.team;

import jakarta.validation.constraints.NotBlank;

public class DeleteTeamRequest {
    @NotBlank public String leaderId;
}