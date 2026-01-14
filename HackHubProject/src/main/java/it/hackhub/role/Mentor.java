package it.hackhub.domain.model;

import it.hackhub.domain.model.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Mentor extends RegisteredUser {
    public Mentor(String username, String email) {
        super(username, email);
        setRole(Role.MENTOR);
    }

    // Visualizza richieste team assignedHackathon, propone call
}
