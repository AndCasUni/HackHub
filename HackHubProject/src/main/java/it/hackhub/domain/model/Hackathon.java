package it.hackhub.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import it.hackhub.domain.state.HackathonState;
import it.hackhub.domain.state.RegistrationState;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Hackathon {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime registrationDeadline;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int maxTeamSize;
    private Organizer organizer;
    private Judge judge;
    private List<Mentor> mentors = new ArrayList<>();
    private List<SubTeam> teams = new ArrayList<>();
    private List<Submission> submissions = new ArrayList<>();
    private SubTeam winner;

    private HackathonState currentState;

    public Hackathon(String name, int maxTeamSize) {
        this.name = name;
        this.maxTeamSize = maxTeamSize;
        this.currentState = new RegistrationState();
    }

    public void setState(HackathonState state) {
        this.currentState = state;
        state.setHackathon(this);
    }

    public void openRegistration() { currentState.openRegistration(this); }
    public void start() { currentState.transitionToRunning(this); }
    public void startEvaluation() { currentState.transitionToEvaluation(this); }
    public void close() { currentState.transitionToClosed(this); }
    public void addTeam(SubTeam team) { currentState.addTeam(this, team); }
    public void addSubmission(Submission submission) {
        currentState.addSubmission(this, submission);
    }

    public boolean canViewSubmissions(User user) {
        return user.isStaff() && this.equals(user.getAssignedHackathon());
    }
}
