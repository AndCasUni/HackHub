package it.hackhub.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leader_id", nullable = false)
    @JsonIgnoreProperties("teams")
    private User leader;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hackathon_id")
    @JsonIgnoreProperties({"registeredTeams", "winner"})
    private Hackathon registeredHackathon;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties("teams")
    private List<User> members = new ArrayList<>();

    @OneToOne(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("team")
    private Submission submission;

    @Column(name = "is_reported")
    private boolean reported = false;

    @Column(name = "report_reason")
    private String reportReason;

    @Column(name = "is_disqualified")
    private boolean disqualified = false;
}