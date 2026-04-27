package it.hackhub.model.domain;

import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.role.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {

    @Id
    @Column(length = 36)
    private String id = java.util.UUID.randomUUID().toString();

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_enum", nullable = false)
    private UserRoleEnum roleEnum;

    @Transient
    private UserRole role;

    public void initializeRole() {
        this.role = switch (this.roleEnum) {
            case ORGANIZER -> new OrganizerRole();
            case JUDGE -> new JudgeRole();
            case MENTOR -> new MentorRole();
            case PLAYER -> new PlayerRole();
        };
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public UserRole getCurrentRole() {
        if (this.role == null) initializeRole();
        return this.role;
    }
}