package it.hackhub.repository;

import it.hackhub.model.domain.Team;
import it.hackhub.config.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class TeamRepository {

    public void save(Team team) {
        try (Session session = HibernateUtil.getSession()) {
            var tx = session.beginTransaction();
            session.merge(team);
            tx.commit();
        }
    }

    public Optional<Team> findById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            Team team = session.get(Team.class, id);
            return Optional.ofNullable(team);
        }
    }

    public List<Team> findByHackathon(String hackathonId) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            "FROM Team WHERE registeredHackathon.id = :hackId", Team.class)
                    .setParameter("hackId", hackathonId)
                    .list();
        }
    }

    public List<Team> findByLeader(String leaderId) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            "FROM Team WHERE leader.id = :leaderId", Team.class)
                    .setParameter("leaderId", leaderId)
                    .list();
        }
    }
}
