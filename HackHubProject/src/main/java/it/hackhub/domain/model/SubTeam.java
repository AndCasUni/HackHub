package it.hackhub.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data @NoArgsConstructor
public class SubTeam {
    private Long id;
    private List<RegisteredUser> participants;
    private RegisteredUser captain;
    private Team parentTeam;

    public SubTeam(List<RegisteredUser> participants, RegisteredUser captain) {
        this.participants = participants;
        this.captain = captain;
        this.parentTeam = captain.getCurrentTeam();
    }

    public boolean hasValidSize(int maxSize) {
        return participants.size() <= maxSize && participants.size() >= 1;
    }
}
