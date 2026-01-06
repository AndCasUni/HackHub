package it.hackhub.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data @NoArgsConstructor
public class Submission {
    private Long id;
    private SubTeam subTeam;
    private String content;
    private LocalDateTime submittedAt;
    private List<Evaluation> evaluations = new ArrayList<>();
    private boolean isFinal = false;

    public void updateContent(String newContent) {
        if (isFinal) throw new IllegalStateException("Scadenza superata");
        this.content = newContent;
        this.submittedAt = LocalDateTime.now();
    }

    public void setFinal() {
        this.isFinal = true;
    }
}
