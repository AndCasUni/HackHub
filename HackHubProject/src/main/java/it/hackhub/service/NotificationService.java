package it.hackhub.service;

import it.hackhub.model.domain.Notification;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.NotificationType;
import it.hackhub.repository.NotificationRepository;
import it.hackhub.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void sendNotification(String userId, String title, String message, NotificationType type) {
        User user = userRepository.findById(userId).orElseThrow();

        Notification notif = new Notification();
        notif.setRecipient(user);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setType(type);
        notif.setCreatedAt(LocalDateTime.now());
        notif.setRead(false);

        notificationRepository.save(notif);
    }

    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findUnreadByUser(userId);
    }

    public void markAsRead(String notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    public void save(Notification notification) {
        notificationRepository.save(notification);
    }
}
