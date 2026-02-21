package it.hackhub.service;

import it.hackhub.model.domain.Notification;
import it.hackhub.model.domain.User;
import it.hackhub.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void sendNotification(User recipient, String title, String message, String type) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        notificationRepository.save(notification);

        System.out.println("[NOTIFICA >> " + recipient.getUsername() + "] " + title + ": " + message);
    }

    public List<Notification> getUnreadUserNotifications(String userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(String notificationId) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Notifica non trovata"));

        notif.setRead(true);
        notificationRepository.save(notif);
    }
    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByRecipientId(userId);
    }
}