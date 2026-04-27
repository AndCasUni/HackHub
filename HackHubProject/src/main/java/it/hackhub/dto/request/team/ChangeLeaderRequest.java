package it.hackhub.dto.request.team;

import jakarta.validation.constraints.NotBlank;

public class ChangeLeaderRequest {
    @NotBlank public String currentLeaderId;
    @NotBlank public String newLeaderId;
}