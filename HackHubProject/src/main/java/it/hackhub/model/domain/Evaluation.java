package it.hackhub.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "judge_id", nullable = false)
    private UserStaff judge;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    @JsonIgnoreProperties({"evaluations", "team"})
    private Submission submission;

    @Column(nullable = false)
    private int score;

    @Column(length = 1000)
    private String feedback;
}