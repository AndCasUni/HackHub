package it.hackhub.service;

import it.hackhub.exception.UserAlreadyInTeamException;
import it.hackhub.model.domain.User;
import it.hackhub.model.enums.UserRoleEnum;
import it.hackhub.repository.UserRepository;
import it.hackhub.role.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(String username, String email, String password, UserRoleEnum roleEnum, String customId) {
        User user = new User();
        if (customId != null && !customId.isBlank()) {
            user.setId(customId);
        }        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoleEnum(roleEnum);

        user.initializeRole();

        return userRepository.save(user);
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
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utente non trovato con ID: " + id));
    }

    public List<User> getUsersByRole(UserRoleEnum role) {
        return userRepository.findByRoleEnum(role);
    }
    public User login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new SecurityException("Credenziali non valide: email o password errate."));
    }
    // MODIFICA PROFILO (Username e Email)
    public User updateProfile(String userId, String newUsername, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Utente non trovato"));


        if (newUsername != null && !newUsername.isEmpty()) {
            user.setUsername(newUsername);
        }
        if (newEmail != null && !newEmail.isEmpty()) {
            user.setEmail(newEmail);
        }

        return userRepository.save(user);
    }

    // RECUPERO PASSWORD
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new java.util.NoSuchElementException("Nessun utente trovato con questa email."));

        user.setPassword(newPassword);
        userRepository.save(user);
    }


}