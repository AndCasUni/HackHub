package it.hackhub.observer;

import it.hackhub.model.domain.Hackathon;
import it.hackhub.model.domain.Team;
import it.hackhub.model.domain.User;

import java.util.ArrayList;
import java.util.List;

public class HackathonSubject {

    private final List<HackathonObserver> observers = new ArrayList<>();

    public void addObserver(HackathonObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(HackathonObserver observer) {
        observers.remove(observer);
    }

    protected void notifyStatusChange(Hackathon hackathon) {
        for (HackathonObserver observer : observers) {
            observer.onHackathonStatusChanged(hackathon);
        }
    }

    protected void notifyStaffAssigned(Hackathon hackathon, User staffMember) {
        for (HackathonObserver observer : observers) {
            observer.onStaffAssigned(hackathon, staffMember);
        }
    }
    protected void notifyTeamWon(Hackathon hackathon, Team winningTeam) {
        for (HackathonObserver observer : observers) {
            observer.onTeamWon(hackathon, winningTeam);
        }
    }

}