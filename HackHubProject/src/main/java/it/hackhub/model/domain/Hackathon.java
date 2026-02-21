package it.hackhub.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.state.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.HashSet;
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

    @Column(length = 32000) // Text
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HackathonStatus state = HackathonStatus.REGISTRATION;

    @Column(name = "prize_amount")
    private Double prizeAmount;

    // Relazione con l'organizzatore (opzionale, ma utile)
    @ManyToOne
    @JoinColumn(name = "organizer_id")
    @JsonIgnoreProperties({"teams", "password"})
    private User organizer;

    @OneToOne
    @JoinColumn(name = "winner_id")
    @JsonIgnoreProperties({"registeredHackathon", "submission", "members"})
    private Team winner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "hackathon_staff",
            joinColumns = @JoinColumn(name = "hackathon_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties({"teams", "password"})
    private Set<User> staff = new HashSet<>();

    @OneToMany(mappedBy = "registeredHackathon", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("registeredHackathon")
    private List<Team> registeredTeams = new ArrayList<>();

    @JsonIgnore
    @Transient
    private HackathonState currentStateObject;

    // --- Metodi di Business Logic ---

    public HackathonState getCurrentStateObject() {
        if (currentStateObject == null) {
            initializeState();
        }
        return currentStateObject;
    }

    public void initializeState() {
        // Se lo stato è nullo, default a REGISTRATION
        if (this.state == null) this.state = HackathonStatus.REGISTRATION;

        this.currentStateObject = switch (this.state) {
            case REGISTRATION -> new RegistrationState();
            case ONGOING -> new OngoingState();
            case EVALUATION -> new EvaluationState();
            case COMPLETED -> new CompletedState();
        };
    }

    public void changeState(HackathonState newState) {
        this.currentStateObject = newState;
        if (newState instanceof RegistrationState) this.state = HackathonStatus.REGISTRATION;
        else if (newState instanceof OngoingState) this.state = HackathonStatus.ONGOING;
        else if (newState instanceof EvaluationState) this.state = HackathonStatus.EVALUATION;
        else if (newState instanceof CompletedState) this.state = HackathonStatus.COMPLETED;
    }

    public Team calculateWinner() {
        if (registeredTeams == null || registeredTeams.isEmpty()) return null;

        Team winner = null;
        double maxScore = -1.0;

        for (Team team : registeredTeams) {

            if (team.getSubmission() != null) {
                double avg = team.getSubmission().getEvaluations().stream()
                        .mapToInt(it.hackhub.model.domain.Evaluation::getScore)
                        .average()
                        .orElse(0.0);

                if (avg > maxScore) {
                    maxScore = avg;
                    winner = team;
                }
            }
        }
        return winner;
    }
}