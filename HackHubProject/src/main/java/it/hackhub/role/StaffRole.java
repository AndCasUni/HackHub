package it.hackhub.role;

public class StaffRole implements UserRole {
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
    public boolean canTransitionHackathonState() { return true; }

    @Override
    public boolean canEvaluateSubmissions() { return false; }

    @Override
    public boolean canManageSupportRequests() { return true; }

    @Override
    public String getRoleName() { return "Staff"; }
}
