package it.hackhub.repository;

import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.enums.HackathonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, String> {

    // Trova tutti gli hackathon in un certo stato
    List<Hackathon> findByState(HackathonStatus state);
}