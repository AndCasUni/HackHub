package it.hackhub.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data @NoArgsConstructor
public class Team {
    private Long id;
    private String name;
    private RegisteredUser leader;
    private List<RegisteredUser> members = new ArrayList<>();
    private List<SubTeam> subTeams = new ArrayList<>();

    public void addMember(RegisteredUser member) {
        if (members.contains(member))
            throw new IllegalArgumentException("Già membro");
        members.add(member);
        member.setCurrentTeam(this);
    }

    public SubTeam createSubTeam(List<RegisteredUser> participants, RegisteredUser captain) {
        SubTeam subTeam = new SubTeam(participants, captain);
        subTeams.add(subTeam);
        return subTeam;
    }

    public boolean isLeader(RegisteredUser user) {
        return leader.equals(user);
    }
}
