package it.hackhub.repository;

import it.hackhub.model.domain.TeamInvitation;
import it.hackhub.model.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Aggiungi questa
import org.springframework.data.jpa.repository.Query;    // Aggiungi questa
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Aggiungi questa

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, String> {

    List<TeamInvitation> findByReceiverId(String receiverId);

    Optional<TeamInvitation> findByReceiverIdAndTeamIdAndStatus(String receiverId, String teamId, InvitationStatus status);

    List<TeamInvitation> findByReceiverIdAndStatus(String receiverId, InvitationStatus status);

    // AGGIUNGI QUESTO METODO:
    @Modifying
    @Transactional
    @Query("DELETE FROM TeamInvitation t WHERE t.team.id = ?1")
    void deleteByTeamId(String teamId);
}