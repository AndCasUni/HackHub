package it.hackhub.dto.request.team;

import jakarta.validation.constraints.NotBlank;

public class LeaveTeamRequest {
    @NotBlank public String userId;
}