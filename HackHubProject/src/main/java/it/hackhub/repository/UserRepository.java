package it.hackhub.repository;

import it.hackhub.exception.UserAlreadyInTeamException;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    public void save(User user) {
        try (Session session = HibernateUtil.getSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
        }
    }

    public Optional<User> findById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        }
    }

    public Optional<User> findByEmail(String email) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResultOptional();
        }
    }

    public List<User> findByRole(UserRoleEnum role) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery(
                            "FROM User WHERE roleEnum = :role", User.class)
                    .setParameter("role", role)
                    .list();
        }
    }
    public boolean isUserInAnyTeam(String userId) {
        try (Session session = HibernateUtil.getSession()) {
            List<Team> teams = session.createQuery(
                            "FROM Team t JOIN t.leader l WHERE l.id = :userId", Team.class)
                    .setParameter("userId", userId)
                    .list();
            return !teams.isEmpty();
        }
    }
}
