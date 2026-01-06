package it.hackhub.cli;

import it.hackhub.domain.singleton.HackHubManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class MainMenu {
    private static final Logger logger = LoggerFactory.getLogger(MainMenu.class);
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = getChoice();
            switch (choice) {
                case 1 -> HackHubManager.getInstance().getHackathonCli().run();
                case 2 -> HackHubManager.getInstance().getTeamCli().run();
                case 3 -> loginUser();
                case 0 -> { logger.info("Uscita HackHub"); running = false; }
                default -> logger.warn("Scelta non valida");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n=== HACKHUB ===");
        System.out.println("1. Gestisci Hackathon");
        System.out.println("2. Gestisci Team");
        System.out.println("3. Login");
        System.out.println("0. Esci");
        System.out.print("Scelta: ");
    }

    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void loginUser() {
        // TODO: AuthService.login()
        System.out.println("Login: Utente loggato (stub)");
    }
}
