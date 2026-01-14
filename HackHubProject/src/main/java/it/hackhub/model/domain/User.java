package it.hackhub.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import it.hackhub.domain.model.enums.Role;

@Data
@NoArgsConstructor
public abstract class User {
    protected Long id;
    protected String username;
    protected String email;
    protected Role role = Role.VISITOR;
    protected Team currentTeam;
    protected Hackathon assignedHackathon;

    public boolean isVisitor() { return role == Role.VISITOR; }
    public boolean isRegistered() { return role == Role.REGISTERED; }
    public boolean hasTeam() { return currentTeam != null; }
    public boolean isStaff() { return role == Role.ORGANIZER || role == Role.JUDGE || role == Role.MENTOR; }

    public void setRole(Role newRole) {
        if (isStaff() && newRole != role)
            throw new IllegalStateException("Staff ruolo esclusivo");
        if (hasTeam() && newRole == Role.REGISTERED)
            throw new IllegalStateException("Già in team, non puoi tornare user");
        this.role = newRole;
    }
}
