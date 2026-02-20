package it.hackhub.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class HackathonSubject {
    private final List<HackathonObserver> observers = new ArrayList<>();

    public void addObserver(HackathonObserver observer) {
        observers.add(observer);
    }

    protected void notifyStatusChange(it.hackhub.model.domain.Hackathon h) {
        observers.forEach(o -> o.onStatusChanged(h));
    }

    protected void notifyStaffAssignment(it.hackhub.model.domain.Hackathon h, it.hackhub.model.domain.User u) {
        observers.forEach(o -> o.onStaffAssigned(h, u));
    }

    protected void notifyInvitation(it.hackhub.model.domain.TeamInvitation i) {
        observers.forEach(o -> o.onInvitationUpdated(i));
    }
}