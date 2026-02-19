package it.hackhub.repository;

import it.hackhub.model.domain.SupportRequest;
import it.hackhub.config.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class SupportRequestRepository {

    public void save(SupportRequest request) {
        try (Session session = HibernateUtil.getSession()) {
            var tx = session.beginTransaction();
            session.merge(request);
            tx.commit();
        }
    }

    public List<SupportRequest> findOpenRequests() {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            "FROM SupportRequest WHERE status = 'OPEN'", SupportRequest.class)
                    .list();
        }
    }

    public List<SupportRequest> findByMentor(String mentorId) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            "FROM SupportRequest WHERE assignedMentor.id = :mentorId",
                            SupportRequest.class)
                    .setParameter("mentorId", mentorId)
                    .list();
        }
    }

    public Optional<SupportRequest> findById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            return Optional.ofNullable(session.get(SupportRequest.class, id));
        }
    }
}
