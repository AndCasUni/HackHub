package it.hackhub.model.domain;

import it.hackhub.model.enums.HackathonStatus;
import it.hackhub.state.*;
import jakarta.persistence.*;
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
public class Hackathon {
    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "prize_amount")
    private float prizeAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private HackathonStatus state = HackathonStatus.REGISTRATION;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "hackathon_staff",
            joinColumns = @JoinColumn(name = "hackathon_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> staff = new ArrayList<>();

    @OneToMany(mappedBy = "registeredHackathon", cascade = CascadeType.ALL)
    private List<Team> registeredTeams = new ArrayList<>();

    // Metodo per aggiungere staff garantendo la bidirezionalità
    public void addStaffMember(User user) {
        if (!this.staff.contains(user)) {
            this.staff.add(user);
            user.getAssignedHackathons().add(this);
        }
    }

    // Metodo per registrare un team (Suggerimento 4)
    public void registerTeam(Team team) {
        if (this.state == HackathonStatus.REGISTRATION) {
            this.registeredTeams.add(team);
            team.setRegisteredHackathon(this);
        }
    }
    public HackathonState getCurrentStateObject() {
        return switch (this.state) {
            case REGISTRATION -> new RegistrationState();
            case ONGOING -> new OngoingState();
            case EVALUATION -> new EvaluationState();
            case COMPLETED -> new CompletedState();
        };
    }
    public boolean allSubmissionsJudged() {
        if (this.registeredTeams.isEmpty()) return false;

        return this.registeredTeams.stream()
                .map(Team::getLatestSubmission)
                .filter(java.util.Objects::nonNull)
                .allMatch(s -> !s.getEvaluations().isEmpty());
    }

}
