package it.hackhub.role;

public class PlayerRole implements UserRole {
    @Override
    public boolean canCreateTeam() { return true; }

    @Override
    public boolean canInviteMembers() { return true; }

    @Override
    public boolean canRegisterTeamToHackathon() { return true; }

    @Override
    public boolean canSubmitProject() { return true; }

    @Override
    public boolean canCreateHackathon() { return false; }

    @Override
    public boolean canAssignStaff() { return false; }

    @Override
    public boolean canTransitionHackathonState() { return false; }

    @Override
    public boolean canEvaluateSubmissions() { return false; }

    @Override
    public boolean canManageSupportRequests() { return false; }

    @Override
    public String getRoleName() { return "Player"; }
}
