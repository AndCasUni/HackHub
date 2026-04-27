package it.hackhub.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequest {
    @NotBlank public String email;
    @NotBlank public String newPassword;
}