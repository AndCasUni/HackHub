package it.hackhub.dto.request.invitation;

import jakarta.validation.constraints.NotBlank;

public class InviteRequest {
    @NotBlank public String senderId;
    @NotBlank public String receiverId;
    @NotBlank public String teamId;
}