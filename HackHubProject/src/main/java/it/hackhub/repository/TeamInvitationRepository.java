package it.hackhub.repository;

import it.hackhub.model.domain.TeamInvitation;
import it.hackhub.config.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class TeamInvitationRepository {

    public void save(TeamInvitation invitation) {
        try (Session session = HibernateUtil.getSession()) {
            var tx = session.beginTransaction();
            session.merge(invitation);
            tx.commit();
        }
    }

    public Optional<TeamInvitation> findById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            return Optional.ofNullable(session.get(TeamInvitation.class, id));
        }
    }

    public List<TeamInvitation> findPendingByReceiver(String receiverId) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            "FROM TeamInvitation WHERE receiver.id = :receiver AND status = 'PENDING'",
                            TeamInvitation.class)
                    .setParameter("receiver", receiverId)
                    .list();
        }
    }
}
