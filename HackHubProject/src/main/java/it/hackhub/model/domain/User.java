package it.hackhub.model.domain;

import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.role.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_enum", nullable = false)
    private UserRoleEnum roleEnum;

    @ManyToMany(mappedBy = "staff")
    private List<Hackathon> assignedHackathons = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<Team> teams = new ArrayList<>();

    @Transient
    private UserRole role;

    public void initializeRole() {
        this.role = switch (this.roleEnum) {
            case ORGANIZER -> new it.hackhub.role.OrganizerRole();
            case JUDGE -> new it.hackhub.role.JudgeRole();
            case MENTOR -> new it.hackhub.role.MentorRole();
            case PLAYER -> new it.hackhub.role.PlayerRole();
            default -> null;
        };
    }

    // Metodo helper per verificare se lo staff è libero (Suggerimento 1)
    public boolean isStaffAvailable() {
        return assignedHackathons.stream()
                .noneMatch(h -> h.getState() != it.hackhub.model.enums.HackathonStatus.COMPLETED);
    }

    public boolean isMemberOfActiveTeam() {
        if (this.teams == null || this.teams.isEmpty()) {
            return false;
        }
        // Verifica se esiste almeno un team che partecipa a un hackathon non ancora COMPLETED
        return this.teams.stream()
                .anyMatch(team -> team.getRegisteredHackathon() == null ||
                        team.getRegisteredHackathon().getState() != it.hackhub.model.enums.HackathonStatus.COMPLETED);
    }
    public boolean isStaffOccupied() {
        if (this.assignedHackathons == null || this.assignedHackathons.isEmpty()) {
            return false;
        }
        // Ritorna true se esiste almeno un hackathon non concluso
        return this.assignedHackathons.stream()
                .anyMatch(h -> h.getState() != it.hackhub.model.enums.HackathonStatus.COMPLETED);
    }

    /**
     * Recupera la sottomissione del team attivo di cui l'utente fa parte.
     */
    public Submission getAssignedSubmission() {
        if (this.teams == null) return null;

        return this.teams.stream()
                // Filtra per i team iscritti a un hackathon non ancora concluso
                .filter(t -> t.getRegisteredHackathon() != null &&
                        t.getRegisteredHackathon().getState() != it.hackhub.model.enums.HackathonStatus.COMPLETED)
                .map(Team::getLatestSubmission)
                .findFirst()
                .orElse(null);
    }

    public UserRole getCurrentRole() {
        if (this.role == null) {
            initializeRole();
        }
        return this.role;
    }
}