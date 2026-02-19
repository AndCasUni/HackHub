package it.hackhub.repository;

import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.config.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class HackathonRepository {

    public void save(Hackathon hackathon) {
        try (Session session = HibernateUtil.getSession()) {
            var tx = session.beginTransaction();
            session.merge(hackathon);
            tx.commit();
        }
    }

    public Hackathon findById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            return session.get(Hackathon.class, id);
        }
    }

    public List<Hackathon> findByStatus(HackathonStatus status) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            "FROM Hackathon WHERE state = :status", Hackathon.class)
                    .setParameter("status", status)
                    .list();
        }
    }
}
