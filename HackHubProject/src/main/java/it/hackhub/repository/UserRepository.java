package it.hackhub.repository;

import it.hackhub.model.domain.User;
import it.hackhub.model.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import it.hackhub.model.domain.UserPlayer;
import it.hackhub.model.domain.UserStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    // Trova utenti per ruolo
    List<User> findByRoleEnum(UserRoleEnum roleEnum);

    // Query personalizzata per verificare se l'utente è in un team (come membro)
    // Ritorna true se il conteggio è > 0
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Team t JOIN t.members m WHERE m.id = :userId")
    boolean isUserInAnyTeam(String userId);

    java.util.Optional<User> findByEmailAndPassword(String email, String password);

    List<UserPlayer> findAllByRoleEnumIn(List<UserRoleEnum> roles);
    List<UserStaff> findByRoleEnumIn(List<UserRoleEnum> staffRoles);

}