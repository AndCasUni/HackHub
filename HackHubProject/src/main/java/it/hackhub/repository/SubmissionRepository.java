package it.hackhub.repository;

import it.hackhub.model.domain.Submission;
import it.hackhub.config.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class SubmissionRepository {

    public void save(Submission submission) {
        try (Session session = HibernateUtil.getSession()) {
            var tx = session.beginTransaction();
            session.merge(submission);
            tx.commit();
        }
    }

    public Submission findById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            return session.get(Submission.class, id);
        }
    }

    public List<Submission> findByHackathon(String hackathonId) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            """
                            FROM Submission s 
                            JOIN FETCH s.team t 
                            JOIN FETCH t.registeredHackathon h 
                            WHERE h.id = :hackId
                            """, Submission.class)
                    .setParameter("hackId", hackathonId)
                    .list();
        }
    }

    public List<Submission> findPendingSubmissions(String hackathonId) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            """
                            FROM Submission s 
                            JOIN s.team t 
                            JOIN t.registeredHackathon h 
                            WHERE h.id = :hackId 
                            AND SIZE(s.evaluations) = 0
                            """, Submission.class)
                    .setParameter("hackId", hackathonId)
                    .list();
        }
    }

    public List<Submission> findByTeam(String teamId) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            "FROM Submission WHERE team.id = :teamId", Submission.class)
                    .setParameter("teamId", teamId)
                    .list();
        }
    }
}
