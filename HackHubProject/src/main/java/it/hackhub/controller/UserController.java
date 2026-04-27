package it.hackhub.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.hackhub.dto.request.user.*;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Utenti", description = "Gestione utenti")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserRequest req) {
        return ResponseEntity.ok(userService.registerUser(req.username, req.email, req.password, req.role, req.id));
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(userService.login(req.email, req.password));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getByRole(@PathVariable UserRoleEnum role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable String id,
                                              @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(userService.updateProfile(id, req.username, req.email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        userService.resetPassword(req.email, req.newPassword);
        return ResponseEntity.ok("Password aggiornata con successo.");
    }
}