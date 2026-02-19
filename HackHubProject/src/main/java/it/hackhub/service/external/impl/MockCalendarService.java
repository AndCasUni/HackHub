package it.hackhub.service.external.impl;

import it.hackhub.service.external.CalendarService;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.User;
import java.time.LocalDateTime;

public class MockCalendarService implements CalendarService {
    @Override
    public boolean bookCall(User mentor, Team team, LocalDateTime dateTime) {
        System.out.println("[EXTERNAL] Slot prenotato sul Calendar esterno per il mentor "
                + mentor.getUsername() + " con il team " + team.getName());
        return true;
    }

    @Override
    public void cancelCall(String callId) {
        System.out.println("[EXTERNAL] Call " + callId + " annullata.");
    }
}