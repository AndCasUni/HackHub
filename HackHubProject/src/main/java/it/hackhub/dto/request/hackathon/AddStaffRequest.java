package it.hackhub.dto.request.hackathon;

import jakarta.validation.constraints.NotBlank;

public class AddStaffRequest {
    @NotBlank public String userId;
}