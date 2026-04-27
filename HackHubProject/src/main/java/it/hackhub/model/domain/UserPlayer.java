package it.hackhub.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_players")
@DiscriminatorValue("PLAYER")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserPlayer extends User {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_team_id")
    @JsonIgnore
    private Team currentTeam;

    @JsonIgnore
    public boolean isMemberOfActiveTeam() {
        return this.currentTeam != null;
    }

    @JsonIgnore
    public Submission getAssignedSubmission() {
        if (this.currentTeam == null) return null;
        return this.currentTeam.getSubmission();
    }
}