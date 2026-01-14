package it.hackhub.domain.model;

import it.hackhub.domain.model.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Judge extends RegisteredUser {
    public Judge(String username, String email) {
        super(username, email);
        setRole(Role.JUDGE);
    }

    // Vede tutte sottomissioni assignedHackathon, assegna punteggi
}
