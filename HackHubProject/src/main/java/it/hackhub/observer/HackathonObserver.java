package it.hackhub.observer;

import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.User;

public interface HackathonObserver {
    void onHackathonStatusChanged(Hackathon hackathon);

    // Notifica quando un membro dello staff viene assegnato
    void onStaffAssigned(Hackathon hackathon, User staffMember);

    void onTeamWon(Hackathon hackathon, Team winningTeam);
}