package it.hackhub.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.hackhub.model.domain.Notification;
import it.hackhub.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notifiche", description = "Gestione notifiche utenti")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // GET http://localhost:8080/api/notifications/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getAllByUser(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    // GET http://localhost:8080/api/notifications/user/{userId}/unread
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadByUser(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUnreadUserNotifications(userId));
    }

    // PATCH http://localhost:8080/api/notifications/{id}/read
    @PatchMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notifica segnata come letta.");
    }
}