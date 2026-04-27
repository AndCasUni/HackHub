package it.hackhub.dto.request.team;

import jakarta.validation.constraints.NotBlank;

public class RegisterHackathonRequest {
    @NotBlank public String hackathonId;
    @NotBlank public String requesterId;
}