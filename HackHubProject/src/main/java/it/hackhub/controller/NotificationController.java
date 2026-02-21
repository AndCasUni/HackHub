package it.hackhub.controller;

import it.hackhub.model.domain.Notification;
import it.hackhub.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // GET: Mostra tutte le notifiche di un utente
    // URL: http://localhost:8080/api/notifications/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
    // GET: Mostra SOLO le notifiche NON LETTE di un utente
    // URL: http://localhost:8080/api/notifications/user/{userId}/unread
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUnreadUserNotifications(userId));
    }

    // POST: Segna una notifica specifica come letta
    // URL: POST http://localhost:8080/api/notifications/{id}/read
    @PostMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable String id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok("Notifica segnata come letta.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}