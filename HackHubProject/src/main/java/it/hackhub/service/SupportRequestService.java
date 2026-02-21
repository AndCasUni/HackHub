package it.hackhub.service;

import it.hackhub.model.domain.*;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.model.enums.SupportRequestStatus;
import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.repository.SupportRequestRepository;
import it.hackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public SupportRequestService(SupportRequestRepository supportRequestRepository, UserRepository userRepository) {
        this.supportRequestRepository = supportRequestRepository;
        this.userRepository = userRepository;
    }

    // CREAZIONE RICHIESTA (Solo Leader, Solo Hackathon ONGOING)
    @Transactional
    public SupportRequest createRequest(String leaderId, String title, String description) {
        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new NoSuchElementException("Utente non trovato"));


        Team activeTeam = leader.getTeams().stream()
                .filter(t -> t.getLeader().getId().equals(leaderId)) // Deve essere il LEADER
                .filter(t -> t.getRegisteredHackathon() != null)     // Deve essere iscritto
                .filter(t -> t.getRegisteredHackathon().getState() == HackathonStatus.ONGOING) // Hackathon deve essere in corso
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("L'utente non è Leader di nessun Team iscritto ad un Hackathon in corso (ONGOING)."));

        SupportRequest request = new SupportRequest();
        request.setRequester(leader);
        request.setTeam(activeTeam);
        request.setTitle(title);
        request.setDescription(description);
        request.setStatus(SupportRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        return supportRequestRepository.save(request);
    }

    @Transactional
    public void acceptRequest(String requestId, String mentorId, LocalDateTime callTime) {
        SupportRequest request = supportRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Richiesta non trovata"));

        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new NoSuchElementException("Mentore non trovato"));

        Hackathon hackathon = request.getTeam().getRegisteredHackathon();

        if (mentor.getRoleEnum() != UserRoleEnum.MENTOR) {
            throw new SecurityException("L'utente non è un MENTOR.");
        }
        if (!hackathon.getStaff().contains(mentor)) {
            throw new SecurityException("Il mentore non fa parte dello staff.");
        }
        if (callTime.isBefore(hackathon.getStartDate()) || callTime.isAfter(hackathon.getEndDate())) {
            throw new IllegalArgumentException("La call deve essere durante l'hackathon.");
        }

        request.setAssignedMentor(mentor);
        request.setScheduledCallTime(callTime);
        request.setStatus(SupportRequestStatus.CALL_PROPOSED);

        supportRequestRepository.save(request);
    }

    // LEADER CONFERMA CALL
    @Transactional
    public void confirmCall(String requestId, String leaderId) {
        SupportRequest request = supportRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Richiesta non trovata"));

        if (!request.getTeam().getLeader().getId().equals(leaderId)) {
            throw new SecurityException("Solo il leader del team può confermare la call.");
        }

        if (request.getStatus() != SupportRequestStatus.CALL_PROPOSED) {
            throw new IllegalStateException("Nessuna call proposta da confermare.");
        }

        request.setStatus(SupportRequestStatus.ACCEPTED);
        supportRequestRepository.save(request);
    }

    // LEADER RIFIUTA CALL
    @Transactional
    public void rejectCall(String requestId, String leaderId) {
        SupportRequest request = supportRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Richiesta non trovata"));

        if (!request.getTeam().getLeader().getId().equals(leaderId)) {
            throw new SecurityException("Solo il leader del team può rifiutare la call.");
        }

        if (request.getStatus() != SupportRequestStatus.CALL_PROPOSED) {
            throw new IllegalStateException("Nessuna call proposta da rifiutare.");
        }

        request.setStatus(SupportRequestStatus.PENDING);
        request.setScheduledCallTime(null);
        request.setAssignedMentor(null);

        supportRequestRepository.save(request);
    }

    // CHIUSURA RICHIESTA
    @Transactional
    public void closeRequest(String requestId, String mentorId) {
        SupportRequest request = supportRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Richiesta non trovata"));

        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new NoSuchElementException("Mentore non trovato"));

        Hackathon hackathon = request.getTeam().getRegisteredHackathon();

        boolean isAssigned = request.getAssignedMentor() != null && request.getAssignedMentor().getId().equals(mentorId);
        boolean isStaff = hackathon.getStaff().contains(mentor);

        if (!isAssigned && !isStaff) {
            throw new SecurityException("Non hai i permessi per chiudere questa richiesta (devi essere il mentore assegnato o staff).");
        }

        request.setStatus(SupportRequestStatus.SOLVED);
        supportRequestRepository.save(request);
    }

    public List<SupportRequest> getRequestsByHackathon(String hackathonId) {
        return supportRequestRepository.findByTeam_RegisteredHackathon_Id(hackathonId);
    }

    public List<SupportRequest> getRequestsByTeam(String teamId) {
        return supportRequestRepository.findByTeamId(teamId);
    }
}