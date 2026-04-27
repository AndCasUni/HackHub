package it.hackhub.config;

import it.hackhub.model.domain.*;
import it.hackhub.model.enums.*;
import it.hackhub.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

        // ─── STAFF ────────────────────────────────────────────────────────────

        UserStaff organizer = new UserStaff();
        organizer.setId("org1");
        organizer.setUsername("Mario Organizer");
        organizer.setEmail("mario@org.it");
        organizer.setPassword("1234");
        organizer.setRoleEnum(UserRoleEnum.ORGANIZER);
        organizer.initializeRole();
        userRepository.save(organizer);

        UserStaff mentor = new UserStaff();
        mentor.setId("mentor1");
        mentor.setUsername("Carlo Mentor");
        mentor.setEmail("carlo@mentor.it");
        mentor.setPassword("1234");
        mentor.setRoleEnum(UserRoleEnum.MENTOR);
        mentor.initializeRole();
        userRepository.save(mentor);

        // judge1 — staff di hack1 (ONGOING) — NON usato per valutare (già occupato)
        UserStaff judge1 = new UserStaff();
        judge1.setId("judge1");
        judge1.setUsername("Sara Judge");
        judge1.setEmail("sara@judge.it");
        judge1.setPassword("1234");
        judge1.setRoleEnum(UserRoleEnum.JUDGE);
        judge1.initializeRole();
        userRepository.save(judge1);

        // judge2 — staff di hack3 (EVALUATION) — usato per UC: valuta/modifica valutazione
        UserStaff judge2 = new UserStaff();
        judge2.setId("judge2");
        judge2.setUsername("Paolo Judge");
        judge2.setEmail("paolo@judge.it");
        judge2.setPassword("1234");
        judge2.setRoleEnum(UserRoleEnum.JUDGE);
        judge2.initializeRole();
        userRepository.save(judge2);

        // ─── PLAYER ───────────────────────────────────────────────────────────

        // player1 — Leader di team1 (hack1 ONGOING) — usa: invita, invia sub, richiedi assistenza
        UserPlayer player1 = new UserPlayer();
        player1.setId("player1");
        player1.setUsername("Luca Leader");
        player1.setEmail("luca@player.it");
        player1.setPassword("1234");
        player1.setRoleEnum(UserRoleEnum.PLAYER);
        player1.initializeRole();
        userRepository.save(player1);

        // player2 — Membro di team1 — usa: abbandona team (POST /teams/team1/leave?userId=player2)
        UserPlayer player2 = new UserPlayer();
        player2.setId("player2");
        player2.setUsername("Anna Member");
        player2.setEmail("anna@player.it");
        player2.setPassword("1234");
        player2.setRoleEnum(UserRoleEnum.PLAYER);
        player2.initializeRole();
        userRepository.save(player2);

        // player3 — Leader di team2 (hack3 EVALUATION) — riceve inv1 da player1
        UserPlayer player3 = new UserPlayer();
        player3.setId("player3");
        player3.setUsername("Marco Beta");
        player3.setEmail("marco@player.it");
        player3.setPassword("1234");
        player3.setRoleEnum(UserRoleEnum.PLAYER);
        player3.initializeRole();
        userRepository.save(player3);

        // player4 — Leader di team3 (libero) — usa: iscrivere team, disiscrizione
        UserPlayer player4 = new UserPlayer();
        player4.setId("player4");
        player4.setUsername("Giorgio Free");
        player4.setEmail("giorgio@player.it");
        player4.setPassword("1234");
        player4.setRoleEnum(UserRoleEnum.PLAYER);
        player4.initializeRole();
        userRepository.save(player4);

        // player5 — Membro di team1 — libero (no currentTeam) — usa: accetta invito (inv2)
        UserPlayer player5 = new UserPlayer();
        player5.setId("player5");
        player5.setUsername("Elena Free");
        player5.setEmail("elena@player.it");
        player5.setPassword("1234");
        player5.setRoleEnum(UserRoleEnum.PLAYER);
        player5.initializeRole();
        userRepository.save(player5);

        // ─── HACKATHON 1 — ONGOING ────────────────────────────────────────────
        // Contiene: team1, staff: mentor1 + judge1
        // Usato per: avvia valutazione, invia sub, richiedi assistenza, proponi call,
        //            segnala violazione, squalifica team

        Hackathon hackOngoing = new Hackathon();
        hackOngoing.setId("hack1");
        hackOngoing.setName("HackHub Spring 2026");
        hackOngoing.setDescription("Hackathon principale in corso");
        hackOngoing.setStartDate(LocalDateTime.of(2026, 4, 1, 9, 0));
        hackOngoing.setEndDate(LocalDateTime.of(2026, 12, 31, 18, 0));
        hackOngoing.setPrizeAmount(1000.0);
        hackOngoing.setMaxParticipants(10);
        hackOngoing.setOrganizer(organizer);
        hackOngoing.setState(HackathonStatus.ONGOING);
        hackathonRepository.save(hackOngoing);

        mentor.setCurrentHackathon(hackOngoing);
        judge1.setCurrentHackathon(hackOngoing);
        hackOngoing.getStaff().add(mentor);
        hackOngoing.getStaff().add(judge1);
        userRepository.save(mentor);
        userRepository.save(judge1);
        hackathonRepository.save(hackOngoing);

        // ─── HACKATHON 2 — REGISTRATION ──────────────────────────────────────
        // Usato per: iscrivere team3, inizia hackathon

        Hackathon hackRegistration = new Hackathon();
        hackRegistration.setId("hack2");
        hackRegistration.setName("HackHub Summer 2026");
        hackRegistration.setDescription("Hackathon in fase di registrazione");
        hackRegistration.setStartDate(LocalDateTime.of(2026, 7, 1, 9, 0));
        hackRegistration.setEndDate(LocalDateTime.of(2026, 7, 5, 18, 0));
        hackRegistration.setPrizeAmount(500.0);
        hackRegistration.setMaxParticipants(10);
        hackRegistration.setOrganizer(organizer);
        hackRegistration.setState(HackathonStatus.REGISTRATION);
        hackathonRepository.save(hackRegistration);

        // ─── HACKATHON 3 — EVALUATION ─────────────────────────────────────────
        // Contiene: team2, staff: judge2
        // Usato per: valuta sottomissione, proclama vincitore

        Hackathon hackEvaluation = new Hackathon();
        hackEvaluation.setId("hack3");
        hackEvaluation.setName("HackHub Winter 2025");
        hackEvaluation.setDescription("Hackathon in fase di valutazione");
        hackEvaluation.setStartDate(LocalDateTime.of(2025, 12, 1, 9, 0));
        hackEvaluation.setEndDate(LocalDateTime.of(2025, 12, 5, 18, 0));
        hackEvaluation.setPrizeAmount(2000.0);
        hackEvaluation.setMaxParticipants(10);
        hackEvaluation.setOrganizer(organizer);
        hackEvaluation.setState(HackathonStatus.EVALUATION);
        hackathonRepository.save(hackEvaluation);

        judge2.setCurrentHackathon(hackEvaluation);
        hackEvaluation.getStaff().add(judge2);
        userRepository.save(judge2);
        hackathonRepository.save(hackEvaluation);

        // ─── TEAM 1 — iscritto a hack1 (ONGOING) ─────────────────────────────
        // membri: player1 (leader), player2
        // Usato per: abbandona team (player2), richiedi assistenza, invio sub

        Team team1 = new Team();
        team1.setId("team1");
        team1.setName("Team Alpha");
        team1.setLeader(player1);
        team1.setRegisteredHackathon(hackOngoing);
        team1.getMembers().add(player1);
        team1.getMembers().add(player2);
        teamRepository.save(team1);

        player1.setCurrentTeam(team1);
        player2.setCurrentTeam(team1);
        userRepository.save(player1);
        userRepository.save(player2);

        hackOngoing.getRegisteredTeams().add(team1);
        hackathonRepository.save(hackOngoing);

        // ─── TEAM 2 — iscritto a hack3 (EVALUATION) ──────────────────────────
        // membri: player3 (leader)
        // Usato per: valuta sottomissione (sub2), proclama vincitore

        Team team2 = new Team();
        team2.setId("team2");
        team2.setName("Team Beta");
        team2.setLeader(player3);
        team2.setRegisteredHackathon(hackEvaluation);
        team2.getMembers().add(player3);
        teamRepository.save(team2);

        player3.setCurrentTeam(team2);
        userRepository.save(player3);

        hackEvaluation.getRegisteredTeams().add(team2);
        hackathonRepository.save(hackEvaluation);

        // ─── TEAM 3 — libero, non iscritto a nessun hackathon ────────────────
        // membri: player4 (leader)
        // Usato per: iscrivere team a hack2, poi disiscrizione

        Team team3 = new Team();
        team3.setId("team3");
        team3.setName("Team Gamma");
        team3.setLeader(player4);
        team3.getMembers().add(player4);
        teamRepository.save(team3);

        player4.setCurrentTeam(team3);
        userRepository.save(player4);

        // ─── INVITO 1 — player1 → player5 (PENDING) ──────────────────────────
        // player5 è libero → flusso positivo: accetta l'invito
        // Usato per: accetta invito (POST /api/invitations/inv1/reply?accepted=true)

        TeamInvitation invitation1 = new TeamInvitation();
        invitation1.setId("inv1");
        invitation1.setSender(player1);
        invitation1.setReceiver(player5);
        invitation1.setTeam(team1);
        invitation1.setStatus(InvitationStatus.PENDING);
        invitation1.setSentAt(LocalDateTime.now());
        teamInvitationRepository.save(invitation1);

        // ─── INVITO 2 — player1 → player3 (PENDING, errore atteso) ──────────
        // player3 è già in team2 → il server risponderà con errore
        // Usato per: testare UC invita utente — flusso alternativo

        TeamInvitation invitation2 = new TeamInvitation();
        invitation2.setId("inv2");
        invitation2.setSender(player1);
        invitation2.setReceiver(player3);
        invitation2.setTeam(team1);
        invitation2.setStatus(InvitationStatus.PENDING);
        invitation2.setSentAt(LocalDateTime.now());
        teamInvitationRepository.save(invitation2);

        // ─── SUBMISSION 1 — team1, hack1 ONGOING ─────────────────────────────

        Submission submission1 = new Submission();
        submission1.setId("sub1");
        submission1.setTeam(team1);
        submission1.setGithubUrl("https://github.com/team-alpha/progetto");
        submission1.setSubmittedAt(LocalDateTime.of(2026, 4, 10, 14, 0));
        submissionRepository.save(submission1);
        team1.setSubmission(submission1);
        teamRepository.save(team1);

        // ─── SUBMISSION 2 — team2, hack3 EVALUATION ──────────────────────────
        // Usato per: valuta sottomissione (judge2), proclama vincitore

        Submission submission2 = new Submission();
        submission2.setId("sub2");
        submission2.setTeam(team2);
        submission2.setGithubUrl("https://github.com/team-beta/progetto");
        submission2.setSubmittedAt(LocalDateTime.of(2025, 12, 3, 10, 0));
        submissionRepository.save(submission2);
        team2.setSubmission(submission2);
        teamRepository.save(team2);

        // ─── EVALUATION — judge2 su sub2 ─────────────────────────────────────
        // eval1: score=7 — già presente per testare UC valuta sottomissione (judge2 su sub2 di hack3)

        Evaluation eval1 = new Evaluation();
        eval1.setId("eval1");
        eval1.setJudge(judge2);
        eval1.setSubmission(submission2);
        eval1.setScore(7);
        eval1.setFeedback("Buon lavoro, ma manca documentazione.");
        evaluationRepository.save(eval1);

        // ─── SUPPORT REQUEST 1 — PENDING ─────────────────────────────────────
        // Usato per: proponi call (mentor1 accetta sup1)

        SupportRequest sup1 = new SupportRequest();
        sup1.setId("sup1");
        sup1.setTitle("Problema tecnico deploy");
        sup1.setDescription("Non riusciamo a configurare il server di produzione");
        sup1.setStatus(SupportRequestStatus.PENDING);
        sup1.setRequester(player1);
        sup1.setTeam(team1);
        sup1.setCreatedAt(LocalDateTime.now());
        supportRequestRepository.save(sup1);

        // ─── SUPPORT REQUEST 2 — CALL_PROPOSED ───────────────────────────────
        // Usato per: conferma call / rifiuta call (player1 su sup2)

        SupportRequest sup2 = new SupportRequest();
        sup2.setId("sup2");
        sup2.setTitle("Problema con il database");
        sup2.setDescription("Errore di connessione al database H2");
        sup2.setStatus(SupportRequestStatus.CALL_PROPOSED);
        sup2.setRequester(player1);
        sup2.setTeam(team1);
        sup2.setAssignedMentor(mentor);
        sup2.setScheduledCallTime(LocalDateTime.of(2026, 6, 15, 10, 0));
        sup2.setCreatedAt(LocalDateTime.now().minusDays(1));
        supportRequestRepository.save(sup2);

        System.out.println("========================================");
        System.out.println(" DataInitializer — Dati caricati!");
        System.out.println(" Utenti: org1, mentor1, judge1, judge2, player1..5");
        System.out.println(" Hackathon: hack1(ONGOING), hack2(REGISTRATION), hack3(EVALUATION)");
        System.out.println(" Staff hack1: mentor1, judge1 | Staff hack3: judge2");
        System.out.println(" Team: team1(hack1, p1+p2), team2(hack3, p3), team3(libero, p4)");
        System.out.println(" Player5: libero, riceve inv1 da player1");
        System.out.println(" Inviti: inv1(p1->p5 PENDING, flusso OK), inv2(p1->p3 PENDING, errore atteso)");
        System.out.println(" Submission: sub1(team1/hack1), sub2(team2/hack3)");
        System.out.println(" Evaluation: eval1(judge2 su sub2, score=7)");
        System.out.println(" Support: sup1(PENDING, mentor può accettare), sup2(CALL_PROPOSED, leader conferma/rifiuta)");
        System.out.println("========================================");
    }
}
