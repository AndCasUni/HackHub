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

        // ══════════════════════════════════════════════════════════════
        //  ORGANIZER
        //  org1 → hack1 (ONGOING)
        //  org2 → hack2 (REGISTRATION)
        //  org3 → hack3 (EVALUATION)
        //  org4 → hack4 (REGISTRATION) → UC "Annulla Hackathon"
        //  org5 → hack_proc (EVALUATION) → UC "Proclama Vincitore"
        //  org6 → hack_eval (ONGOING)   → UC "Avviare Valutazione"
        //  org7 → hack_reg (REGISTRATION)→ UC "Iscrivere/Disiscrizione/Aggiungi Staff"
        //  org8 → LIBERO                → UC "Crea Hackathon"
        // ══════════════════════════════════════════════════════════════
        UserStaff org1 = createStaff("org1", "Mario Org1",   "org1@hack.it", UserRoleEnum.ORGANIZER);
        UserStaff org2 = createStaff("org2", "Luigi Org2",   "org2@hack.it", UserRoleEnum.ORGANIZER);
        UserStaff org3 = createStaff("org3", "Giulia Org3",  "org3@hack.it", UserRoleEnum.ORGANIZER);
        UserStaff org4 = createStaff("org4", "Sara Org4",    "org4@hack.it", UserRoleEnum.ORGANIZER);
        UserStaff org5 = createStaff("org5", "Fabio Org5",   "org5@hack.it", UserRoleEnum.ORGANIZER);
        UserStaff org6 = createStaff("org6", "Chiara Org6",  "org6@hack.it", UserRoleEnum.ORGANIZER);
        UserStaff org7 = createStaff("org7", "Davide Org7",  "org7@hack.it", UserRoleEnum.ORGANIZER);
        UserStaff org8 = createStaff("org8", "Irene Org8",   "org8@hack.it", UserRoleEnum.ORGANIZER);

        // ══════════════════════════════════════════════════════════════
        //  MENTOR
        //  mentor1 → hack1 | mentor2 → hack2 | mentor3 → hack3
        //  mentor4 → hack_proc
        // ══════════════════════════════════════════════════════════════
        UserStaff mentor1 = createStaff("mentor1", "Carlo Mentor1", "mentor1@hack.it", UserRoleEnum.MENTOR);
        UserStaff mentor2 = createStaff("mentor2", "Anna Mentor2",  "mentor2@hack.it", UserRoleEnum.MENTOR);
        UserStaff mentor3 = createStaff("mentor3", "Rosa Mentor3",  "mentor3@hack.it", UserRoleEnum.MENTOR);
        UserStaff mentor4 = createStaff("mentor4", "Enzo Mentor4",  "mentor4@hack.it", UserRoleEnum.MENTOR);

        // ══════════════════════════════════════════════════════════════
        //  JUDGE
        //  judge1 → hack1 | judge2 → hack2 | judge3 → hack3
        //  judge4 → LIBERO → UC "Aggiungi Staff" (viene aggiunto a hack_reg)
        //  judge5 → hack_proc
        // ══════════════════════════════════════════════════════════════
        UserStaff judge1 = createStaff("judge1", "Sara Judge1",  "judge1@hack.it", UserRoleEnum.JUDGE);
        UserStaff judge2 = createStaff("judge2", "Paolo Judge2", "judge2@hack.it", UserRoleEnum.JUDGE);
        UserStaff judge3 = createStaff("judge3", "Marco Judge3", "judge3@hack.it", UserRoleEnum.JUDGE);
        UserStaff judge4 = createStaff("judge4", "Elena Judge4", "judge4@hack.it", UserRoleEnum.JUDGE);
        UserStaff judge5 = createStaff("judge5", "Luca Judge5",  "judge5@hack.it", UserRoleEnum.JUDGE);

        // ══════════════════════════════════════════════════════════════
        //  PLAYER
        //  player1  → leader team1 (hack1)
        //  player2  → membro team1 → UC "Abbandona Team"
        //  player3  → leader team2 (hack3)
        //  player4  → leader team3 (LIBERO) → UC "Iscrivere Team"
        //  player5  → LIBERO, riceve inv1   → UC "Accetta Invito"
        //  player6  → LIBERO, nessun inv    → UC "Crea Team"
        //  player7  → leader team4 (hack2) → pre-iscritto per "Avvia Hackathon"
        //  player8  → leader team5 (hack1) → UC "Squalifica Team" (team dedicato)
        //  player9  → leader team6 (hack_reg) → UC "Disiscrizione Team"
        //  player10 → leader team7 (hack_proc) → per "Proclama Vincitore"
        //  player11 → LIBERO, nessun inv   → UC "Invita Utenti" (receiver)
        // ══════════════════════════════════════════════════════════════
        UserPlayer player1  = createPlayer("player1",  "Luca Leader",   "player1@hack.it");
        UserPlayer player2  = createPlayer("player2",  "Anna Member",   "player2@hack.it");
        UserPlayer player3  = createPlayer("player3",  "Marco Beta",    "player3@hack.it");
        UserPlayer player4  = createPlayer("player4",  "Giorgio Free",  "player4@hack.it");
        UserPlayer player5  = createPlayer("player5",  "Elena Free",    "player5@hack.it");
        UserPlayer player6  = createPlayer("player6",  "Sofia Free",    "player6@hack.it");
        UserPlayer player7  = createPlayer("player7",  "Riccardo T4",   "player7@hack.it");
        UserPlayer player8  = createPlayer("player8",  "Beatrice Sq",   "player8@hack.it");
        UserPlayer player9  = createPlayer("player9",  "Nicola Dereg",  "player9@hack.it");
        UserPlayer player10 = createPlayer("player10", "Valentina Proc","player10@hack.it");
        UserPlayer player11 = createPlayer("player11", "Tommaso Free",  "player11@hack.it");

        // ══════════════════════════════════════════════════════════════
        //  HACK 1 — ONGOING
        //  Staff: mentor1 + judge1
        //  Team: team1 (player1+player2) + team5 (player8, per Squalifica)
        //  ⚠ sub1 NON precaricata → "Invia Sottomissione" è INDIPENDENTE
        //  ⚠ hack1 rimane ONGOING → non usata da "Avviare Valutazione" (usa hack_eval)
        // ══════════════════════════════════════════════════════════════
        Hackathon hack1 = new Hackathon();
        hack1.setId("hack1");
        hack1.setName("HackHub Spring 2026");
        hack1.setDescription("Hackathon principale in corso");
        hack1.setStartDate(LocalDateTime.of(2026, 4, 1, 9, 0));
        hack1.setEndDate(LocalDateTime.of(2026, 12, 31, 18, 0));
        hack1.setPrizeAmount(1000.0);
        hack1.setMaxParticipants(50);
        hack1.setOrganizer(org1);
        hack1.setState(HackathonStatus.ONGOING);
        hackathonRepository.save(hack1);
        assignStaff(mentor1, hack1);
        assignStaff(judge1, hack1);
        hackathonRepository.save(hack1);

        // ── team1 ──────────────────────────────────────────────────────
        Team team1 = new Team();
        team1.setId("team1");
        team1.setName("Team Alpha");
        team1.setLeader(player1);
        team1.setRegisteredHackathon(hack1);
        team1.getMembers().add(player1);
        team1.getMembers().add(player2);
        teamRepository.save(team1);
        player1.setCurrentTeam(team1); userRepository.save(player1);
        player2.setCurrentTeam(team1); userRepository.save(player2);
        hack1.getRegisteredTeams().add(team1);
        hackathonRepository.save(hack1);

        // ── team5 (team_sq) — dedicato a "Squalifica Team" ─────────────
        // Rimosso dopo squalifica → team1 rimane intatto per altri test
        Team team5 = new Team();
        team5.setId("team5");
        team5.setName("Team Sigma");
        team5.setLeader(player8);
        team5.setRegisteredHackathon(hack1);
        team5.getMembers().add(player8);
        team5.setReported(true);
        teamRepository.save(team5);
        player8.setCurrentTeam(team5); userRepository.save(player8);
        hack1.getRegisteredTeams().add(team5);
        hackathonRepository.save(hack1);

        // ══════════════════════════════════════════════════════════════
        //  HACK 2 — REGISTRATION
        //  Staff: mentor2 + judge2  |  Team: team4 (player7) pre-iscritto
        //  → "Avvia Hackathon" è INDIPENDENTE (ha staff + almeno 1 team)
        //  → Non toccato da "Iscrivere Team" (usa hack_reg)
        // ══════════════════════════════════════════════════════════════
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

        Team team4 = new Team();
        team4.setId("team4");
        team4.setName("Team Delta");
        team4.setLeader(player7);
        team4.setRegisteredHackathon(hack2);
        team4.getMembers().add(player7);
        teamRepository.save(team4);
        player7.setCurrentTeam(team4); userRepository.save(player7);
        hack2.getRegisteredTeams().add(team4);
        hackathonRepository.save(hack2);

        // ══════════════════════════════════════════════════════════════
        //  HACK 3 — EVALUATION
        //  Staff: mentor3 + judge3  |  Team: team2 (player3) + sub2
        //  → "Valuta Sottomissione": judge3 valuta sub2 (sub2 non ha eval)
        //  → hack3 rimane EVALUATION → non toccata da "Proclama" (usa hack_proc)
        // ══════════════════════════════════════════════════════════════
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

        Team team2 = new Team();
        team2.setId("team2");
        team2.setName("Team Beta");
        team2.setLeader(player3);
        team2.setRegisteredHackathon(hack3);
        team2.getMembers().add(player3);
        teamRepository.save(team2);
        player3.setCurrentTeam(team2); userRepository.save(player3);
        hack3.getRegisteredTeams().add(team2);
        hackathonRepository.save(hack3);

        Submission sub2 = new Submission();
        sub2.setId("sub2");
        sub2.setTeam(team2);
        sub2.setGithubUrl("https://github.com/team-beta/progetto");
        sub2.setSubmittedAt(LocalDateTime.of(2025, 12, 3, 10, 0));
        submissionRepository.save(sub2);
        team2.setSubmission(sub2);
        teamRepository.save(team2);

        // ══════════════════════════════════════════════════════════════
        //  HACK 4 — REGISTRATION (nessun staff, nessun team)
        //  → UC "Annulla Hackathon": org4 cancella hack4
        //  → Nessun altro UC usa hack4
        // ══════════════════════════════════════════════════════════════
        Hackathon hack4 = new Hackathon();
        hack4.setId("hack4");
        hack4.setName("HackHub Cancelled 2026");
        hack4.setDescription("Hackathon da annullare");
        hack4.setStartDate(LocalDateTime.of(2026, 9, 1, 9, 0));
        hack4.setEndDate(LocalDateTime.of(2026, 9, 5, 18, 0));
        hack4.setPrizeAmount(300.0);
        hack4.setMaxParticipants(10);
        hack4.setOrganizer(org4);
        hack4.setState(HackathonStatus.REGISTRATION);
        hackathonRepository.save(hack4);

        // ══════════════════════════════════════════════════════════════
        //  HACK_PROC — EVALUATION (per Proclama Vincitore)
        //  Staff: mentor4 + judge5  |  Team: team7 + sub7 + eval_proc precaricata
        //  → "Proclama Vincitore" è INDIPENDENTE (eval esiste già)
        //  → Separato da hack3 → "Valuta Sottomissione" su hack3 non crea conflitti
        // ══════════════════════════════════════════════════════════════
        Hackathon hackProc = new Hackathon();
        hackProc.setId("hackProc");
        hackProc.setName("HackHub Finale 2025");
        hackProc.setDescription("Hackathon in fase di valutazione — proclama pronto");
        hackProc.setStartDate(LocalDateTime.of(2025, 11, 1, 9, 0));
        hackProc.setEndDate(LocalDateTime.of(2025, 11, 5, 18, 0));
        hackProc.setPrizeAmount(3000.0);
        hackProc.setMaxParticipants(20);
        hackProc.setOrganizer(org5);
        hackProc.setState(HackathonStatus.EVALUATION);
        hackathonRepository.save(hackProc);
        assignStaff(mentor4, hackProc);
        assignStaff(judge5, hackProc);
        hackathonRepository.save(hackProc);

        Team team7 = new Team();
        team7.setId("team7");
        team7.setName("Team Omega");
        team7.setLeader(player10);
        team7.setRegisteredHackathon(hackProc);
        team7.getMembers().add(player10);
        teamRepository.save(team7);
        player10.setCurrentTeam(team7); userRepository.save(player10);
        hackProc.getRegisteredTeams().add(team7);
        hackathonRepository.save(hackProc);

        Submission sub7 = new Submission();
        sub7.setId("sub7");
        sub7.setTeam(team7);
        sub7.setGithubUrl("https://github.com/team-omega/finale");
        sub7.setSubmittedAt(LocalDateTime.of(2025, 11, 3, 14, 0));
        submissionRepository.save(sub7);
        team7.setSubmission(sub7);
        teamRepository.save(team7);

        Evaluation evalProc = new Evaluation();
        evalProc.setId("evalProc");
        evalProc.setJudge(judge5);
        evalProc.setSubmission(sub7);
        evalProc.setScore(92);
        evalProc.setFeedback("Progetto eccellente, architettura solida e demo funzionante");
        evaluationRepository.save(evalProc);

        // ══════════════════════════════════════════════════════════════
        //  HACK_EVAL — ONGOING (dedicato ad "Avviare Fase di Valutazione")
        //  Nessun staff, nessun team necessario per forceStateToEvaluation
        //  → Non tocca hack1 (ONGOING) → "Invia Sub" e "Richiedi Assist." restano OK
        // ══════════════════════════════════════════════════════════════
        Hackathon hackEval = new Hackathon();
        hackEval.setId("hackEval");
        hackEval.setName("HackHub Eval Test 2026");
        hackEval.setDescription("Hackathon dedicato al test avanzamento di stato");
        hackEval.setStartDate(LocalDateTime.of(2026, 5, 1, 9, 0));
        hackEval.setEndDate(LocalDateTime.of(2026, 5, 5, 18, 0));
        hackEval.setPrizeAmount(100.0);
        hackEval.setMaxParticipants(10);
        hackEval.setOrganizer(org6);
        hackEval.setState(HackathonStatus.ONGOING);
        hackathonRepository.save(hackEval);

        // ══════════════════════════════════════════════════════════════
        //  HACK_REG — REGISTRATION (per Iscrivere, Disiscrizione, Aggiungi Staff)
        //  Nessun staff inizialmente → judge4 viene aggiunto nel test "Aggiungi Staff"
        //  team3 (LIBERO) si iscrive nel test "Iscrivere Team"
        //  team6 (pre-iscritto) si disiscrive nel test "Disiscrizione Team"
        // ══════════════════════════════════════════════════════════════
        Hackathon hackReg = new Hackathon();
        hackReg.setId("hackReg");
        hackReg.setName("HackHub Open Registration 2026");
        hackReg.setDescription("Hackathon aperto alle iscrizioni");
        hackReg.setStartDate(LocalDateTime.of(2026, 8, 1, 9, 0));
        hackReg.setEndDate(LocalDateTime.of(2026, 8, 5, 18, 0));
        hackReg.setPrizeAmount(200.0);
        hackReg.setMaxParticipants(30);
        hackReg.setOrganizer(org7);
        hackReg.setState(HackathonStatus.REGISTRATION);
        hackathonRepository.save(hackReg);

        // ── team3 LIBERO (player4) — si iscrive a hackReg nel test ─────
        Team team3 = new Team();
        team3.setId("team3");
        team3.setName("Team Gamma");
        team3.setLeader(player4);
        team3.getMembers().add(player4);
        teamRepository.save(team3);
        player4.setCurrentTeam(team3); userRepository.save(player4);

        // ── team6 pre-iscritto a hackReg — per "Disiscrizione Team" ─────
        Team team6 = new Team();
        team6.setId("team6");
        team6.setName("Team Zeta");
        team6.setLeader(player9);
        team6.setRegisteredHackathon(hackReg);
        team6.getMembers().add(player9);
        teamRepository.save(team6);
        player9.setCurrentTeam(team6); userRepository.save(player9);
        hackReg.getRegisteredTeams().add(team6);
        hackathonRepository.save(hackReg);

        // ══════════════════════════════════════════════════════════════
        //  INVITI
        //  inv1: player1→player5 PENDING → "Accetta Invito" INDIPENDENTE
        //        player5 è libero → accetta ed entra in team1
        //  inv2: player1→player3 PENDING → test errore (player3 già in team2)
        // ══════════════════════════════════════════════════════════════
        // ── team_inv: LIBERO, dedicato ai test "Invita Utente" e "Accetta Invito" ──
        UserPlayer player12 = createPlayer("player12", "Giulio Leader", "player12@hack.it");

        Team teamInv = new Team();
        teamInv.setId("teamInv");
        teamInv.setName("Team Inviti");
        teamInv.setLeader(player12);
        teamInv.getMembers().add(player12);
        teamRepository.save(teamInv);
        player12.setCurrentTeam(teamInv);
        userRepository.save(player12);

// inv1: player12 → player5 su teamInv (team LIBERO → check hackathon non scatta)
        TeamInvitation inv1 = new TeamInvitation();
        inv1.setId("inv1");
        inv1.setSender(player12);
        inv1.setReceiver(player5);
        inv1.setTeam(teamInv);
        inv1.setStatus(InvitationStatus.PENDING);
        inv1.setSentAt(LocalDateTime.now().minusHours(1));
        teamInvitationRepository.save(inv1);
        TeamInvitation inv2 = new TeamInvitation();
        inv2.setId("inv2");
        inv2.setSender(player1);
        inv2.setReceiver(player3);
        inv2.setTeam(team1);
        inv2.setStatus(InvitationStatus.PENDING);
        inv2.setSentAt(LocalDateTime.now().minusMinutes(30));
        teamInvitationRepository.save(inv2);

        // ══════════════════════════════════════════════════════════════
        //  SUPPORT REQUESTS (team1 / hack1)
        //  sup1: PENDING, nessun mentor → "Proponi Call" (mentor1 accetta) INDIPENDENTE
        //  sup2: PENDING, nessun mentor → dato extra (lettura)
        //  sup3: PENDING, nessun mentor → dato extra (lettura)
        //  sup4: ACCEPTED, mentor1      → "Chiudi Richiesta" (mentor1 chiude) INDIPENDENTE
        //  ⚠ sup2/sup3 PENDING (non assegnate) → mentor1 NON ha call attive → accetta sup1 senza blocchi
        // ══════════════════════════════════════════════════════════════
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
        sup2.setStatus(SupportRequestStatus.PENDING);
        sup2.setRequester(player1);
        sup2.setTeam(team1);
        sup2.setCreatedAt(LocalDateTime.now().minusHours(2));
        supportRequestRepository.save(sup2);

        SupportRequest sup3 = new SupportRequest();
        sup3.setId("sup3");
        sup3.setTitle("Difficoltà algoritmo ranking");
        sup3.setDescription("Non riusciamo a implementare l'ordinamento delle valutazioni");
        sup3.setStatus(SupportRequestStatus.PENDING);
        sup3.setRequester(player1);
        sup3.setTeam(team1);
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

        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║           DataInitializer v4 — Caricamento completato            ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║ ORGANIZER: org1(hack1) org2(hack2) org3(hack3) org4(hack4)       ║");
        System.out.println("║            org5(hackProc) org6(hackEval) org7(hackReg) org8(FREE)║");
        System.out.println("║ MENTOR:    mentor1(hack1) mentor2(hack2) mentor3(hack3)          ║");
        System.out.println("║            mentor4(hackProc)                                     ║");
        System.out.println("║ JUDGE:     judge1(hack1) judge2(hack2) judge3(hack3)             ║");
        System.out.println("║            judge4(LIBERO→hackReg) judge5(hackProc)               ║");
        System.out.println("║ PLAYER:    p1(leader t1) p2(membro t1) p3(leader t2)            ║");
        System.out.println("║            p4(leader t3/FREE) p5(FREE,inv1) p6(FREE,crea team)  ║");
        System.out.println("║            p7(leader t4/hack2) p8(leader t5/hack1-sq)           ║");
        System.out.println("║            p9(leader t6/hackReg) p10(leader t7/hackProc)        ║");
        System.out.println("║            p11(FREE, receiver invito)                            ║");
        System.out.println("║ HACKATHON: hack1=ONGOING  hack2=REGISTRATION  hack3=EVALUATION  ║");
        System.out.println("║            hack4=REGISTRATION(annulla) hackEval=ONGOING(forzaval)║");
        System.out.println("║            hackReg=REGISTRATION(iscrizione) hackProc=EVALUATION ║");
        System.out.println("║ TEAM:      t1(h1,p1+p2) t2(h3,p3) t3(FREE,p4) t4(h2,p7)       ║");
        System.out.println("║            t5(h1,p8-SQUALIFICA) t6(hReg,p9-DISISCRIVI)          ║");
        System.out.println("║            t7(hProc,p10-PROCLAMA)                               ║");
        System.out.println("║ SUB:       sub2(t2/h3,no eval) sub7(t7/hProc,eval precaricata)  ║");
        System.out.println("║ EVAL:      evalProc(judge5,sub7,score=92) — proclama INDIP.     ║");
        System.out.println("║ INVITI:    inv1(p1→p5/t1 PENDING) inv2(p1→p3/t1 PENDING-ERR)   ║");
        System.out.println("║ SUPPORT:   sup1-3(PENDING) sup4(ACCEPTED,mentor1)               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
    }

    private UserStaff createStaff(String id, String username, String email, UserRoleEnum role) {
        UserStaff u = new UserStaff();
        u.setId(id); u.setUsername(username); u.setEmail(email);
        u.setPassword("1234"); u.setRoleEnum(role); u.initializeRole();
        return userRepository.save(u);
    }

    private UserPlayer createPlayer(String id, String username, String email) {
        UserPlayer u = new UserPlayer();
        u.setId(id); u.setUsername(username); u.setEmail(email);
        u.setPassword("1234"); u.setRoleEnum(UserRoleEnum.PLAYER); u.initializeRole();
        return userRepository.save(u);
    }

    private void assignStaff(UserStaff staffMember, Hackathon hackathon) {
        staffMember.setCurrentHackathon(hackathon);
        hackathon.getStaff().add(staffMember);
        userRepository.save(staffMember);
    }
}