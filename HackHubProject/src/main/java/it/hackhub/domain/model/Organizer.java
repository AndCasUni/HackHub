package it.hackhub.domain.model;

import it.hackhub.domain.model.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Organizer extends RegisteredUser {
    public Organizer(String username, String email) {
        super(username, email);
        setRole(Role.ORGANIZER);
    }

    // Può creare Hackathon, assegnare Judge/Mentor, vedere sottomissioni assignedHackathon
}
