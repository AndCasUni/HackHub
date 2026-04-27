package it.hackhub.dto.request.user;

import it.hackhub.model.enums.UserRoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterUserRequest {
    public String id;
    @NotBlank public String username;
    @NotBlank public String email;
    @NotBlank public String password;
    @NotNull  public UserRoleEnum role;
}