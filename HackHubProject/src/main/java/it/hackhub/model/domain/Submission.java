package it.hackhub.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @OneToOne
    @JoinColumn(name = "team_id")
    @JsonIgnoreProperties({"submission", "registeredHackathon"})
    private Team team;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("submission")
    private List<Evaluation> evaluations = new ArrayList<>();
}