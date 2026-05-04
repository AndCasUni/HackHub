package it.hackhub.config;

import it.hackhub.model.domain.*;
import it.hackhub.model.enums.*;
import it.hackhub.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * DataInitializer v2 — Dati allineati alla Collection v4
 *
 * MODIFICHE rispetto alla v1:
 *  - Aggiunto player7 (libero) → usato in "Invitare Utente"
 *  - inv1 ora: player4 → player5, team3 (team3 LIBERO, non team1 ONGOING)
 *  - inv2 invariata: player1 → player3, team1 (player3 già in team2 → errore atteso)
 */
@Component
public class DataInitializer {

    @Autowired private UserRepository userRepository;
    @Autowired private HackathonRepository hackathonRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private TeamInvitationRepository teamInvitationRepository;
    @Autowired private SubmissionRepository submissionRepository;
    @Autowired private SupportRequestRepository supportRequestRepository;
    @Autowired private EvaluationRepository evaluationRepository;

    @PostConstruct
    @Transactional
    public void init() {
        if (userRepository.count() > 0) return;

        // ================================================================
        // ORGANIZER
        // org1 → hack1 (ONGOING)   org2 → hack2 (REGISTRATION)
        // org3 → hack3 (EVALUATION) org4 → LIBERO (test Crea Hackathon)
        // ================================================================
        UserStaff org1 = createStaff("org1", "Mario Org1",   "org1@hack.it",   UserRoleEnum.ORGANIZER);
        UserStaff org2 = createStaff("org2", "Luigi Org2",   "org2@hack.it",   UserRoleEnum.ORGANIZER);
        UserStaff org3 = createStaff("org3", "Giulia Org3",  "org3@hack.it",   UserRoleEnum.ORGANIZER);
        UserStaff org4 = createStaff("org4", "Sara Org4",    "org4@hack.it",   UserRoleEnum.ORGANIZER);

        // ================================================================
        // MENTOR  (mentor1→hack1, mentor2→hack2, mentor3→hack3)
        // ================================================================
        UserStaff mentor1 = createStaff("mentor1", "Carlo Mentor1", "mentor1@hack.it", UserRoleEnum.MENTOR);
        UserStaff mentor2 = createStaff("mentor2", "Anna Mentor2",  "mentor2@hack.it", UserRoleEnum.MENTOR);
        UserStaff mentor3 = createStaff("mentor3", "Rosa Mentor3",  "mentor3@hack.it", UserRoleEnum.MENTOR);

        // ================================================================
        // JUDGE  (judge1→hack1, judge2→hack2, judge3→hack3, judge4 LIBERO)
        // ================================================================
        UserStaff judge1 = createStaff("judge1", "Sara Judge1",  "judge1@hack.it", UserRoleEnum.JUDGE);
        UserStaff judge2 = createStaff("judge2", "Paolo Judge2", "judge2@hack.it", UserRoleEnum.JUDGE);
        UserStaff judge3 = createStaff("judge3", "Marco Judge3", "judge3@hack.it", UserRoleEnum.JUDGE);
        UserStaff judge4 = createStaff("judge4", "Elena Judge4", "judge4@hack.it", UserRoleEnum.JUDGE);

        // ================================================================
        // PLAYER
        // player1 = leader team1 (hack1)  → UC: invia sub, richiedi assistenza
        // player2 = membro team1          → UC: abbandona team
        // player3 = leader team2 (hack3)  → riceve inv2 (già in team → errore atteso)
        // player4 = leader team3 (libero) → UC: iscrivere team, accetta inv1 come sender
        // player5 = LIBERO, riceve inv1   → UC: accetta invito
        // player6 = LIBERO                → UC: creare team (team4)
        // player7 = LIBERO ← NUOVO       → UC: invitare utente (ricevente)
        // ================================================================
        UserPlayer player1 = createPlayer("player1", "Luca Leader",   "player1@hack.it");
        UserPlayer player2 = createPlayer("player2", "Anna Member",   "player2@hack.it");
        UserPlayer player3 = createPlayer("player3", "Marco Beta",    "player3@hack.it");
        UserPlayer player4 = createPlayer("player4", "Giorgio Free",  "player4@hack.it");
        UserPlayer player5 = createPlayer("player5", "Elena Free",    "player5@hack.it");
        UserPlayer player6 = createPlayer("player6", "Sofia Free",    "player6@hack.it");
        UserPlayer player7 = createPlayer("player7", "Luca Free",     "player7@hack.it"); // NUOVO

        // ================================================================
        // HACKATHON 1 — ONGOING (staff: mentor1, judge1 | team: team1)
        // ================================================================
        Hackathon hack1 = new Hackathon();
        hack1.setId("hack1");
        hack1.setName("HackHub Spring 2026");
        hack1.setDescription("Hackathon principale in corso");
        hack1.setStartDate(LocalDateTime.of(2026, 4, 1, 9, 0));
        hack1.setEndDate(LocalDateTime.of(2026, 12, 31, 18, 0));
        hack1.setPrizeAmount(1000.0);
        hack1.setMaxParticipants(20);
        hack1.setOrganizer(org1);
        hack1.setState(HackathonStatus.ONGOING);
        hackathonRepository.save(hack1);
        assignStaff(mentor1, hack1);
        assignStaff(judge1, hack1);
        hackathonRepository.save(hack1);

        // ================================================================
        // HACKATHON 2 — REGISTRATION (staff: mentor2, judge2 | nessun team)
        // team3 si iscrive nel test "Iscrivere Team" → hack2 poi avviato
        // ================================================================
        Hackathon hack2 = new Hackathon();
        hack2.setId("hack2");
        hack2.setName("HackHub Summer 2026");
        hack2.setDescription("Hackathon in fase di registrazione");
        hack2.setStartDate(LocalDateTime.of(2026, 7, 1, 9, 0));
        hack2.setEndDate(LocalDateTime.of(2026, 7, 5, 18, 0));
        hack2.setPrizeAmount(500.0);
        hack2.setMaxParticipants(20);
        hack2.setOrganizer(org2);
        hack2.setState(HackathonStatus.REGISTRATION);
        hackathonRepository.save(hack2);
        assignStaff(mentor2, hack2);
        assignStaff(judge2, hack2);
        hackathonRepository.save(hack2);

        // ================================================================
        // HACKATHON 3 — EVALUATION (staff: mentor3, judge3 | team: team2 + sub2)
        // sub2 valutata nel test "Valutare Sottomissione" → poi Proclama Vincitore
        // ================================================================
        Hackathon hack3 = new Hackathon();
        hack3.setId("hack3");
        hack3.setName("HackHub Winter 2025");
        hack3.setDescription("Hackathon in fase di valutazione");
        hack3.setStartDate(LocalDateTime.of(2025, 12, 1, 9, 0));
        hack3.setEndDate(LocalDateTime.of(2025, 12, 5, 18, 0));
        hack3.setPrizeAmount(2000.0);
        hack3.setMaxParticipants(20);
        hack3.setOrganizer(org3);
        hack3.setState(HackathonStatus.EVALUATION);
        hackathonRepository.save(hack3);
        assignStaff(mentor3, hack3);
        assignStaff(judge3, hack3);
        hackathonRepository.save(hack3);

        // ================================================================
        // TEAM 1 — iscritto hack1 ONGOING (leader: player1, membro: player2)
        // sub1 NON precaricata → creata nel test "Inviare Sottomissione"
        // ================================================================
        Team team1 = new Team();
        team1.setId("team1");
        team1.setName("Team Alpha");
        team1.setLeader(player1);
        team1.setRegisteredHackathon(hack1);
        team1.getMembers().add(player1);
        team1.getMembers().add(player2);
        teamRepository.save(team1);
        player1.setCurrentTeam(team1);
        player2.setCurrentTeam(team1);
        userRepository.save(player1);
        userRepository.save(player2);
        hack1.getRegisteredTeams().add(team1);
        hackathonRepository.save(hack1);

        // ================================================================
        // TEAM 2 — iscritto hack3 EVALUATION (leader: player3)
        // sub2 precaricata ma NON valutata → valutata nel test
        // ================================================================
        Team team2 = new Team();
        team2.setId("team2");
        team2.setName("Team Beta");
        team2.setLeader(player3);
        team2.setRegisteredHackathon(hack3);
        team2.getMembers().add(player3);
        teamRepository.save(team2);
        player3.setCurrentTeam(team2);
        userRepository.save(player3);
        hack3.getRegisteredTeams().add(team2);
        hackathonRepository.save(hack3);

        // ================================================================
        // TEAM 3 — LIBERO (leader: player4)
        // Usato per: Iscrivere Team → hack2, Accetta Invito (player5 entra)
        // ================================================================
        Team team3 = new Team();
        team3.setId("team3");
        team3.setName("Team Gamma");
        team3.setLeader(player4);
        team3.getMembers().add(player4);
        teamRepository.save(team3);
        player4.setCurrentTeam(team3);
        userRepository.save(player4);

        // ================================================================
        // SUBMISSION 2 — team2, hack3 (NON valutata)
        // sub1 NON precaricata → creata nel test
        // ================================================================
        Submission sub2 = new Submission();
        sub2.setId("sub2");
        sub2.setTeam(team2);
        sub2.setGithubUrl("https://github.com/team-beta/progetto");
        sub2.setSubmittedAt(LocalDateTime.of(2025, 12, 3, 10, 0));
        submissionRepository.save(sub2);
        team2.setSubmission(sub2);
        teamRepository.save(team2);

        // ================================================================
        // INVITI
        // inv1: player4 → player5, team3 PENDING   ← MODIFICATO (era team1)
        //   → player5 è libero, team3 è libero → "Accetta Invito" funziona
        // inv2: player1 → player3, team1 PENDING    ← invariato (errore atteso)
        //   → player3 è già in team2 → "accettare invito" deve dare errore
        // ================================================================
        TeamInvitation inv1 = new TeamInvitation();
        inv1.setId("inv1");
        inv1.setSender(player4);   // player4 = leader team3
        inv1.setReceiver(player5); // player5 = libero
        inv1.setTeam(team3);       // team3 = libero (non in hackathon attivo)
        inv1.setStatus(InvitationStatus.PENDING);
        inv1.setSentAt(LocalDateTime.now());
        teamInvitationRepository.save(inv1);

        TeamInvitation inv2 = new TeamInvitation();
        inv2.setId("inv2");
        inv2.setSender(player1);   // player1 = leader team1
        inv2.setReceiver(player3); // player3 = già in team2 → errore atteso
        inv2.setTeam(team1);
        inv2.setStatus(InvitationStatus.PENDING);
        inv2.setSentAt(LocalDateTime.now());
        teamInvitationRepository.save(inv2);

        // ================================================================
        // SUPPORT REQUESTS
        // sup1 PENDING    → test "Proporre Call" (mentor1 accetta)
        // sup2 CALLPROPOSED → precaricato per altri test (conferma call, ecc.)
        // ================================================================
        SupportRequest sup1 = new SupportRequest();
        sup1.setId("sup1");
        sup1.setTitle("Problema tecnico deploy");
        sup1.setDescription("Non riusciamo a configurare il server di produzione");
        sup1.setStatus(SupportRequestStatus.PENDING);
        sup1.setRequester(player1);
        sup1.setTeam(team1);
        sup1.setCreatedAt(LocalDateTime.now().minusHours(3));
        supportRequestRepository.save(sup1);

        SupportRequest sup2 = new SupportRequest();
        sup2.setId("sup2");
        sup2.setTitle("Problema con il database");
        sup2.setDescription("Errore di connessione al database H2");
        sup2.setStatus(SupportRequestStatus.CALL_PROPOSED);
        sup2.setRequester(player1);
        sup2.setTeam(team1);
        sup2.setAssignedMentor(mentor1);
        sup2.setScheduledCallTime(LocalDateTime.of(2026, 6, 15, 10, 0));
        sup2.setCreatedAt(LocalDateTime.now().minusHours(2));
        supportRequestRepository.save(sup2);

        SupportRequest sup3 = new SupportRequest();
        sup3.setId("sup3");
        sup3.setTitle("Difficoltà algoritmo ranking");
        sup3.setDescription("Non riusciamo a implementare l'ordinamento delle valutazioni");
        sup3.setStatus(SupportRequestStatus.CALL_PROPOSED);
        sup3.setRequester(player1);
        sup3.setTeam(team1);
        sup3.setAssignedMentor(mentor1);
        sup3.setScheduledCallTime(LocalDateTime.of(2026, 6, 20, 14, 0));
        sup3.setCreatedAt(LocalDateTime.now().minusHours(1));
        supportRequestRepository.save(sup3);

        SupportRequest sup4 = new SupportRequest();
        sup4.setId("sup4");
        sup4.setTitle("Problema di autenticazione");
        sup4.setDescription("Il sistema di login non funziona correttamente");
        sup4.setStatus(SupportRequestStatus.ACCEPTED);
        sup4.setRequester(player1);
        sup4.setTeam(team1);
        sup4.setAssignedMentor(mentor1);
        sup4.setScheduledCallTime(LocalDateTime.of(2026, 6, 10, 11, 0));
        sup4.setCreatedAt(LocalDateTime.now().minusDays(1));
        supportRequestRepository.save(sup4);

        System.out.println("\n======= DataInitializer: Dati caricati con successo =======");
        System.out.println("Organizer:  org1→hack1  org2→hack2  org3→hack3  org4→LIBERO");
        System.out.println("Mentor:     mentor1→hack1  mentor2→hack2  mentor3→hack3");
        System.out.println("Judge:      judge1→hack1  judge2→hack2  judge3→hack3  judge4→LIBERO");
        System.out.println("Player:     p1(leader team1)  p2(membro team1)  p3(leader team2)");
        System.out.println("            p4(leader team3)  p5(LIBERO,inv1)  p6(LIBERO)  p7(LIBERO,NEW)");
        System.out.println("Hackathon:  hack1→ONGOING  hack2→REGISTRATION  hack3→EVALUATION");
        System.out.println("Team:       team1(hack1,p1+p2)  team2(hack3,p3)  team3(FREE,p4)");
        System.out.println("Sub:        sub2(team2,hack3) — sub1 NON precaricata");
        System.out.println("Inviti:     inv1(p4→p5, team3 PENDING)  inv2(p1→p3, team1 PENDING→errore)");
        System.out.println("Support:    sup1(PENDING)  sup2(CALLPROPOSED)  sup3(CALLPROPOSED)  sup4(ACCEPTED)");
        System.out.println("===========================================================\n");
    }

    private UserStaff createStaff(String id, String username, String email, UserRoleEnum role) {
        UserStaff u = new UserStaff();
        u.setId(id);
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword("1234");
        u.setRoleEnum(role);
        u.initializeRole();
        return userRepository.save(u);
    }

    private UserPlayer createPlayer(String id, String username, String email) {
        UserPlayer u = new UserPlayer();
        u.setId(id);
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword("1234");
        u.setRoleEnum(UserRoleEnum.PLAYER);
        u.initializeRole();
        return userRepository.save(u);
    }

    private void assignStaff(UserStaff staffMember, Hackathon hackathon) {
        staffMember.setCurrentHackathon(hackathon);
        hackathon.getStaff().add(staffMember);
        userRepository.save(staffMember);
    }
}
