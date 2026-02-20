package it.hackhub.role;

public class OrganizerRole implements UserRole {
    @Override
    public boolean canCreateTeam() { return true; }

    @Override
    public boolean canInviteMembers() { return true; }

    @Override
    public boolean canRegisterTeamToHackathon() { return true; }

    @Override
    public boolean canSubmitProject() { return true; }

    @Override
    public boolean canCreateHackathon() { return true; }

    @Override
    public boolean canAssignStaff() { return true; }

    @Override
    public boolean canTransitionHackathonState() { return true; }

    @Override
    public boolean canEvaluateSubmissions() { return false; }

    @Override
    public boolean canManageSupportRequests() { return true; }

    @Override
    public String getRoleName() { return "Organizer"; }
}
