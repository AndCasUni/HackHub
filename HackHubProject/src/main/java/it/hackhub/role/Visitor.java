package it.hackhub.domain.model;

import it.hackhub.domain.model.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Visitor extends User {
    public Visitor(String username, String email) {
        this.username = username;
        this.email = email;
        this.role = Role.VISITOR;
    }

    @Override
    public void setRole(Role newRole) {
        if (newRole == Role.VISITOR) return;
        super.setRole(newRole);
    }
}
