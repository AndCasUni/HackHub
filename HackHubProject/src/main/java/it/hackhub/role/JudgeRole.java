package it.hackhub.role;

public class JudgeRole implements UserRole {
    @Override
    public boolean canCreateTeam() { return false; }

    @Override
    public boolean canInviteMembers() { return false; }

    @Override
    public boolean canRegisterTeamToHackathon() { return false; }

    @Override
    public boolean canSubmitProject() { return false; }

    @Override
    public boolean canCreateHackathon() { return false; }

    @Override
    public boolean canAssignStaff() { return false; }

    @Override
    public boolean canTransitionHackathonState() { return false; }

    @Override
    public boolean canEvaluateSubmissions() { return true; }

    @Override
    public boolean canManageSupportRequests() { return false; }

    @Override
    public String getRoleName() { return "Judge"; }
}
