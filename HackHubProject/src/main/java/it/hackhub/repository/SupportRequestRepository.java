package it.hackhub.repository;

import it.hackhub.model.domain.SupportRequest;
import it.hackhub.model.enums.SupportRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportRequestRepository extends JpaRepository<SupportRequest, String> {
    List<SupportRequest> findByStatus(SupportRequestStatus status);
    List<SupportRequest> findByTeam_RegisteredHackathon_Id(String hackathonId);
    List<SupportRequest> findByTeamId(String teamId);
}