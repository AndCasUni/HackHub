package it.hackhub.observer;

import it.hackhub.model.domain.TeamInvitation;

public interface InvitationObserver {
    void onInvitationSent(TeamInvitation invitation);
    void onInvitationReplied(TeamInvitation invitation);
}