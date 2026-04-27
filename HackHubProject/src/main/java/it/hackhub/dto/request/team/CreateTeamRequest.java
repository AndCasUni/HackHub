package it.hackhub.dto.request.team;

import jakarta.validation.constraints.NotBlank;

public class CreateTeamRequest {
    public String id;
    @NotBlank public String name;
    @NotBlank public String leaderId;
}