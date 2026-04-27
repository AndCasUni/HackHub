package it.hackhub.dto.request.team;

import jakarta.validation.constraints.NotBlank;

public class KickMemberRequest {
    @NotBlank public String leaderId;
}