package it.hackhub.role;

public class StaffRole implements UserRole {

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
    public boolean canAssignStaff() { return true; }

    @Override
    public boolean canTransitionHackathonState() { return true; }

    @Override
    public boolean canEvaluateSubmissions() { return false; }

    @Override
    public boolean canManageSupportRequests() { return true; }

    @Override
    public String getRoleName() { return "Staff"; }
}