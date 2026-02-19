package it.hackhub.repository;

import it.hackhub.model.domain.Evaluation;
import it.hackhub.config.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class EvaluationRepository {

    public void save(Evaluation evaluation) {
        try (Session session = HibernateUtil.getSession()) {
            var tx = session.beginTransaction();
            session.merge(evaluation);
            tx.commit();
        }
    }

    public Evaluation findById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            return session.get(Evaluation.class, id);
        }
    }

    public List<Evaluation> findBySubmission(String submissionId) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            "FROM Evaluation WHERE submission.id = :submissionId", Evaluation.class)
                    .setParameter("submissionId", submissionId)
                    .list();
        }
    }

    public List<Evaluation> findByJudge(String judgeId) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            "FROM Evaluation WHERE judge.id = :judgeId", Evaluation.class)
                    .setParameter("judgeId", judgeId)
                    .list();
        }
    }

    public double getAverageScore(String submissionId) {
        try (Session session = HibernateUtil.getSession()) {
            Double avg = session.createQuery(
                            "SELECT AVG(e.score) FROM Evaluation e WHERE e.submission.id = :id", Double.class)
                    .setParameter("id", submissionId)
                    .uniqueResult();
            return avg != null ? avg : 0.0;
        }
    }
}
