package it.hackhub.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor
public class Evaluation {
    private Long id;
    private Judge judge;
    private Submission submission;
    private String comment;
    private Integer score;
    private LocalDateTime evaluatedAt;

    public boolean isValid() {
        return score != null && score >= 0 && score <= 10;
    }
}
