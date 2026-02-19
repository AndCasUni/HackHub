package it.hackhub.repository;

import it.hackhub.model.domain.Notification;
import it.hackhub.config.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class NotificationRepository {

    public void save(Notification notification) {
        try (Session session = HibernateUtil.getSession()) {
            var tx = session.beginTransaction();
            session.merge(notification);
            tx.commit();
        }
    }

    public List<Notification> findUnreadByUser(String userId) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            "FROM Notification WHERE recipient.id = :userId AND read = false",
                            Notification.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }

    public void markAsRead(String notificationId) {
        try (Session session = HibernateUtil.getSession()) {
            var tx = session.beginTransaction();
            Notification notif = session.get(Notification.class, notificationId);
            if (notif != null) {
                notif.setRead(true);
                session.merge(notif);
            }
            tx.commit();
        }
    }
}
