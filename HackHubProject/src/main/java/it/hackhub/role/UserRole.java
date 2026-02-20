package it.hackhub.role;

public interface UserRole {
    boolean canCreateTeam();
    boolean canInviteMembers();
    boolean canRegisterTeamToHackathon();
    boolean canSubmitProject();
    boolean canCreateHackathon();
    boolean canAssignStaff();
    boolean canTransitionHackathonState();
    boolean canEvaluateSubmissions();
    boolean canManageSupportRequests();

    String getRoleName();
}
