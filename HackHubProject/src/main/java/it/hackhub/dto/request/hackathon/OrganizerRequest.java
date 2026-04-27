package it.hackhub.dto.request.hackathon;

import jakarta.validation.constraints.NotBlank;

public class OrganizerRequest {
    @NotBlank
    public String organizerId;
}