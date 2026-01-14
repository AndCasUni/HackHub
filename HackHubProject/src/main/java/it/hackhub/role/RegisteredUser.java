package it.hackhub.domain.model;

import it.hackhub.domain.model.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisteredUser extends User {

    public RegisteredUser(String username, String email) {
        this.username = username;
        this.email = email;
        this.role = Role.REGISTERED;
    }
}
