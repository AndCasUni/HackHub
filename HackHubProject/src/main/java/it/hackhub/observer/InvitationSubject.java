package it.hackhub.observer;

import it.hackhub.model.domain.TeamInvitation;
import java.util.ArrayList;
import java.util.List;

public class InvitationSubject {
    private final List<InvitationObserver> observers = new ArrayList<>();

    public void addObserver(InvitationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(InvitationObserver observer) {
        observers.remove(observer);
    }

    protected void notifyInvitationSent(TeamInvitation invitation) {
        for (InvitationObserver observer : observers) {
            observer.onInvitationSent(invitation);
        }
    }

    protected void notifyInvitationReplied(TeamInvitation invitation) {
        for (InvitationObserver observer : observers) {
            observer.onInvitationReplied(invitation);
        }
    }
}