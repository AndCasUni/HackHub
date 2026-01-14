package it.hackhub.domain.model.enums;

public enum Role {
    VISITOR,
    REGISTERED,
    TEAM_MEMBER,
    ORGANIZER,
    JUDGE,
    MENTOR;

    public boolean isStaff() {
        return this == ORGANIZER || this == JUDGE || this == MENTOR;
    }
}
