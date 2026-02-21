package it.hackhub.repository;

import it.hackhub.model.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByRecipientId(String recipientId);
    List<Notification> findByRecipientIdAndIsReadFalse(String recipientId);}