package it.hackhub.dto.request.submission;

import jakarta.validation.constraints.NotBlank;

public class SubmitRequest {
    public String id;
    @NotBlank public String teamId;
    @NotBlank public String githubUrl;
    @NotBlank public String submitterId;
}