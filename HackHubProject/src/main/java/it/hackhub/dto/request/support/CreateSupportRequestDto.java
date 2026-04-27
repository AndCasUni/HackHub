package it.hackhub.dto.request.support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateSupportRequestDto {
    @NotBlank public String leaderId;
    @NotBlank @Size(min = 3, max = 100) public String title;
    @NotBlank @Size(max = 500) public String description;
}