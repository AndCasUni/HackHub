package it.hackhub.service;

import it.hackhub.model.domain.SupportRequest;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.SupportRequestStatus;
import it.hackhub.repository.SupportRequestRepository;
import it.hackhub.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

public class SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;

    public SupportRequestService(SupportRequestRepository supportRequestRepository,
                                 UserRepository userRepository) {
        this.supportRequestRepository = supportRequestRepository;
        this.userRepository = userRepository;
    }

    public SupportRequest createRequest(String requesterId, String title, String description) {
        User requester = userRepository.findById(requesterId).orElseThrow();

        SupportRequest req = new SupportRequest();
        req.setRequester(requester);
        req.setTitle(title);
        req.setDescription(description);
        req.setCreatedAt(LocalDateTime.now());

        supportRequestRepository.save(req);
        return req;
    }

    public List<SupportRequest> getOpenRequests() {
        return supportRequestRepository.findOpenRequests();
    }

    public void assignMentor(String requestId, String mentorId) {
        // 1. Recupero del mentore e della richiesta tramite i repository
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentore non trovato"));

        SupportRequest request = supportRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Richiesta di supporto non trovata"));

        // 2. Vincolo: Verifica che l'utente abbia effettivamente il ruolo di MENTOR
        if (mentor.getRoleEnum() != it.hackhub.model.enums.UserRoleEnum.MENTOR) {
            throw new IllegalStateException("L'utente selezionato non è un mentore.");
        }

        // 3. Vincolo: Verifica disponibilità (libero/occupato)
        // Utilizziamo il metodo isStaffOccupied() che abbiamo implementato in User.java
        if (mentor.isStaffOccupied()) {
            throw new it.hackhub.exception.StaffMemberAlreadyOccupiedException(mentorId);
        }

        // 4. Assegnazione e aggiornamento dello stato della richiesta [cite: 80]
        request.setAssignedMentor(mentor);
        request.setStatus(SupportRequestStatus.IN_PROGRESS);

        // 5. Salvataggio delle modifiche
        supportRequestRepository.save(request);

    }
}
