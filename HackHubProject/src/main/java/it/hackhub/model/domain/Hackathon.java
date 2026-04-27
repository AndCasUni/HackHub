package it.hackhub.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.state.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "hackathons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hackathon {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 32000)
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HackathonStatus state = HackathonStatus.REGISTRATION;

    @Column(name = "prize_amount")
    private Double prizeAmount;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    @JsonIgnoreProperties({"currentTeam", "currentHackathon", "password"})
    private User organizer;

    @OneToOne
    @JoinColumn(name = "winner_id")
    @JsonIgnoreProperties({"registeredHackathon", "submission", "members"})
    private Team winner;

    @OneToMany(mappedBy = "currentHackathon", fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"currentHackathon", "password"})
    private List<UserStaff> staff = new ArrayList<>();

    @OneToMany(mappedBy = "registeredHackathon", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("registeredHackathon")
    private List<Team> registeredTeams = new ArrayList<>();

    @JsonIgnore
    @Transient
    private HackathonState currentStateObject;


    public HackathonState getCurrentStateObject() {
        if (currentStateObject == null) initializeState();
        return currentStateObject;
    }

    public void initializeState() {
        if (this.state == null) this.state = HackathonStatus.REGISTRATION;
        this.currentStateObject = switch (this.state) {
            case REGISTRATION -> new RegistrationState();
            case ONGOING      -> new OngoingState();
            case EVALUATION   -> new EvaluationState();
            case COMPLETED    -> new CompletedState();
        };
    }

    public void changeState(HackathonState newState) {
        this.currentStateObject = newState;
        if (newState instanceof RegistrationState) this.state = HackathonStatus.REGISTRATION;
        else if (newState instanceof OngoingState)  this.state = HackathonStatus.ONGOING;
        else if (newState instanceof EvaluationState) this.state = HackathonStatus.EVALUATION;
        else if (newState instanceof CompletedState)  this.state = HackathonStatus.COMPLETED;
    }


    public Team calculateWinner() {
        if (registeredTeams == null || registeredTeams.isEmpty()) return null;

        // Escludo team squalificati dal conteggio
        long totalSubmissions = registeredTeams.stream()
                .filter(t -> !t.isDisqualified())
                .filter(t -> t.getSubmission() != null)
                .count();

        long evaluatedSubmissions = registeredTeams.stream()
                .filter(t -> !t.isDisqualified())
                .filter(t -> t.getSubmission() != null)
                .filter(t -> t.getSubmission().getEvaluations() != null
                        && !t.getSubmission().getEvaluations().isEmpty())
                .count();

        if (totalSubmissions > 0 && evaluatedSubmissions < totalSubmissions)
            throw new IllegalStateException(
                    "Impossibile dichiarare il vincitore: non tutte le sottomissioni sono state valutate.");

        Team winner = null;
        double maxScore = -1.0;

        for (Team team : registeredTeams) {
            if (team.isDisqualified()) continue;

            if (team.getSubmission() == null
                    || team.getSubmission().getEvaluations() == null
                    || team.getSubmission().getEvaluations().isEmpty()) continue;

            double avg = team.getSubmission().getEvaluations().stream()
                    .mapToInt(Evaluation::getScore)
                    .average()
                    .orElse(0.0);

            if (avg > maxScore) {
                maxScore = avg;
                winner = team;
            } else if (avg == maxScore && winner != null) {
                LocalDateTime currentWinnerTime = winner.getSubmission().getSubmittedAt();
                LocalDateTime newTeamTime = team.getSubmission().getSubmittedAt();
                if (newTeamTime != null && currentWinnerTime != null
                        && newTeamTime.isBefore(currentWinnerTime)) {
                    winner = team;
                }
            }
        }

        return winner;
    }
}