package it.hackhub.dto.request.team;

import jakarta.validation.constraints.NotBlank;

public class UnregisterRequest {
    @NotBlank public String leaderId;
}