package it.hackhub.service;

import it.hackhub.exception.UserAlreadyInTeamException;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.role.UserRole;
import it.hackhub.repository.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String email, String password, UserRoleEnum roleEnum) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoleEnum(roleEnum);
        user.initializeRole();
        userRepository.save(user);
        return user;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public boolean can(User user, String action) {
        UserRole role = user.getCurrentRole();
        return switch (action) {
            case "CREATE_TEAM" -> role.canCreateTeam();
            case "INVITE_MEMBERS" -> role.canInviteMembers();
            case "REGISTER_HACKATHON" -> role.canRegisterTeamToHackathon();
            case "SUBMIT_PROJECT" -> role.canSubmitProject();
            case "CREATE_HACKATHON" -> role.canCreateHackathon();
            case "ASSIGN_STAFF" -> role.canAssignStaff();
            case "TRANSITION_HACKATHON" -> role.canTransitionHackathonState();
            case "EVALUATE_SUBMISSIONS" -> role.canEvaluateSubmissions();
            case "MANAGE_SUPPORT" -> role.canManageSupportRequests();
            default -> false;
        };
    }

    public void ensureUserNotInTeam(String userId) {
        if (userRepository.isUserInAnyTeam(userId)) {
            throw new UserAlreadyInTeamException(userId);
        }
    }
}
