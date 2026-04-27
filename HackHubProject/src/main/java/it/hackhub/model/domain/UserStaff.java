package it.hackhub.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_staff")
@DiscriminatorValue("STAFF")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserStaff extends User {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_hackathon_id")
    @JsonIgnore
    private Hackathon currentHackathon;

    @JsonIgnore
    public boolean isStaffAvailable() {
        return this.currentHackathon == null;
    }

    @JsonIgnore
    public boolean isStaffOccupied() {
        return this.currentHackathon != null;
    }
}