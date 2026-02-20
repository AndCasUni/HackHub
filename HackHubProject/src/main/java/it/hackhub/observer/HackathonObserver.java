package it.hackhub.observer;

import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.TeamInvitation;
import it.hackhub.model.domain.User;

public interface HackathonObserver {
    void onStatusChanged(Hackathon hackathon);

    void onStaffAssigned(Hackathon hackathon, User staffMember);

    void onInvitationUpdated(TeamInvitation invitation);
}