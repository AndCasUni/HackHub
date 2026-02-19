package it.hackhub.service.external;

import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.User;
import java.time.LocalDateTime;

/**
 * Interfaccia per il sistema esterno di Calendar
 */
public interface CalendarService {
    /**
     * Delega la prenotazione dello slot al sistema esterno
     */
    boolean bookCall(User mentor, Team team, LocalDateTime dateTime);

    void cancelCall(String callId);
}