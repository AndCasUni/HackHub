package it.hackhub.cli;

import it.hackhub.domain.singleton.HackHubManager;
import it.hackhub.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class TeamCli {
    private static final Logger logger = LoggerFactory.getLogger(TeamCli.class);
    private final Scanner scanner = new Scanner(System.in);
    private final TeamService service = HackHubManager.getInstance().getTeamService();

    public void run() {
        boolean running = true;
        while (running) {
            printTeamMenu();
            int choice = getChoice();
            switch (choice) {
                case 1 -> listTeams();
                case 2 -> createTeam();
                case 3 -> inviteMember();
                case 4 -> subscribeHackathon();
                case 0 -> running = false;
                default -> logger.warn("Scelta non valida");
            }
        }
    }

    private void printTeamMenu() {
        System.out.println("\n=== TEAM ===");
        System.out.println("1. Lista Team");
        System.out.println("2. Crea Team");
        System.out.println("3. Invita Membro");
        System.out.println("4. Iscrivi Hackathon (sottoteam)");
        System.out.println("0. Indietro");
    }

    private void listTeams() { /* Stub */ }
    private void createTeam() { /* Stub */ }
    private void inviteMember() {
        System.out.println("Invito inviato (Observer notifica)");
    }
    private void subscribeHackathon() { /* Stub */ }

    private int getChoice() {
        try { return Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { return -1; }
    }
}
