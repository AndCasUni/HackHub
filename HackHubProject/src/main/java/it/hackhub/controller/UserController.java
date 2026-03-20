package it.hackhub.controller;

import it.hackhub.model.domain.User;
import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    public static class RegisterRequest {
        public String id;
        public String username;
        public String email;
        public String password;
        public UserRoleEnum role;
    }
    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class UpdateProfileRequest {
        public String username;
        public String email;
    }

    public static class ResetPasswordRequest {
        public String email;
        public String newPassword;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest req) {
        User created = userService.registerUser(req.username, req.email, req.password, req.role, req.id);
        return ResponseEntity.ok(created);
    }
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getByRole(@PathVariable UserRoleEnum role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            User loggedInUser = userService.login(req.email, req.password);
            return ResponseEntity.ok(loggedInUser);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
    // PUT: Modifica Profilo Utente
    // URL: PUT http://localhost:8080/api/users/{id}?username=NuovoNome&email=nuova@mail.it
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable String id, @RequestBody UpdateProfileRequest req) {
        try {
            User updatedUser = userService.updateProfile(id, req.username, req.email);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Errore aggiornamento: " + e.getMessage());
        }
    }

    // POST: Recupero Password (Reset)
    // URL: POST http://localhost:8080/api/users/reset-password?email=test@test.it&newPassword=nuovaPass123
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest req) {
        try {
            userService.resetPassword(req.email, req.newPassword);
            return ResponseEntity.ok("Password aggiornata con successo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}