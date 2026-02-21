package it.hackhub.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.role.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_enum", nullable = false)
    private UserRoleEnum roleEnum;

    @ManyToMany(mappedBy = "staff", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Hackathon> assignedHackathons = new ArrayList<>();

    @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Team> teams = new ArrayList<>();

    @Transient
    private UserRole role;


    // --- Metodi di utilità (Logica di Business) ---

    public void initializeRole() {
        this.role = switch (this.roleEnum) {
            case ORGANIZER -> new it.hackhub.role.OrganizerRole();
            case JUDGE -> new it.hackhub.role.JudgeRole();
            case MENTOR -> new it.hackhub.role.MentorRole();
            case PLAYER -> new it.hackhub.role.PlayerRole();
        };
    }

    @JsonIgnore
    public UserRole getCurrentRole() {
        if (this.role == null) {
            initializeRole();
        }
        return this.role;
    }

    @JsonIgnore
    public boolean isStaffAvailable() {
        return assignedHackathons.stream()
                .noneMatch(h -> h.getState() != it.hackhub.model.enums.HackathonStatus.COMPLETED);
    }
    @JsonIgnore
    public boolean isMemberOfActiveTeam() {
        if (this.teams == null || this.teams.isEmpty()) return false;
        return this.teams.stream()
                .anyMatch(team -> team.getRegisteredHackathon() == null ||
                        team.getRegisteredHackathon().getState() != it.hackhub.model.enums.HackathonStatus.COMPLETED);
    }

    @JsonIgnore
    public boolean isStaffOccupied() {
        if (this.assignedHackathons == null || this.assignedHackathons.isEmpty()) return false;
        return this.assignedHackathons.stream()
                .anyMatch(h -> h.getState() != it.hackhub.model.enums.HackathonStatus.COMPLETED);
    }

    @JsonIgnore
    public Submission getAssignedSubmission() {
        if (this.teams == null) return null;
        return this.teams.stream()
                .filter(t -> t.getRegisteredHackathon() != null &&
                        t.getRegisteredHackathon().getState() != it.hackhub.model.enums.HackathonStatus.COMPLETED)
                .map(Team::getSubmission)
                .findFirst()
                .orElse(null);
    }
}