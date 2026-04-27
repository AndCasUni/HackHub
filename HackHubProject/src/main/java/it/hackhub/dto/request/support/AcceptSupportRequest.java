package it.hackhub.dto.request.support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AcceptSupportRequest {
    @NotBlank public String mentorId;
    @NotNull  public LocalDateTime callTime;
}