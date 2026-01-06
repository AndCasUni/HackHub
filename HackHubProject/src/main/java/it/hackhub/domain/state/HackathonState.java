package it.hackhub.domain.state;

import it.hackhub.domain.model.Hackathon;
import it.hackhub.domain.model.SubTeam;
import it.hackhub.domain.model.Submission;

public interface HackathonState {
    void addTeam(Hackathon hackathon, SubTeam team);
    void addSubmission(Hackathon hackathon, Submission submission);
    void transitionToRunning(Hackathon hackathon);
    void transitionToEvaluation(Hackathon hackathon);
    void transitionToClosed(Hackathon hackathon);
    void evaluateSubmission(Hackathon hackathon, Submission submission);
    String getStatus();

    void openRegistration(Hackathon hackathon);
    void setHackathon(Hackathon hackathon);

}
