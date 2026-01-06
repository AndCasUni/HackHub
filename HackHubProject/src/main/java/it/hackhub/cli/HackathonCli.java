package it.hackhub.cli;

import it.hackhub.domain.model.Hackathon;
import it.hackhub.domain.singleton.HackHubManager;
import it.hackhub.service.HackathonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class HackathonCli {
    private static final Logger logger = LoggerFactory.getLogger(HackathonCli.class);
    private final Scanner scanner = new Scanner(System.in);
    private final HackathonService service = HackHubManager.getInstance().getHackathonService();

    public void run() {
        boolean running = true;
        while (running) {
            printHackathonMenu();
            int choice = getChoice();
            switch (choice) {
                case 1 -> listHackathons();
                case 2 -> createHackathon();
                case 3 -> viewHackathon();
                case 0 -> running = false;
                default -> logger.warn("Scelta non valida");
            }
        }
    }

    private void printHackathonMenu() {
        System.out.println("\n=== HACKATHON ===");
        System.out.println("1. Lista Hackathon");
        System.out.println("2. Crea Hackathon");
        System.out.println("3. Visualizza Hackathon");
        System.out.println("0. Indietro");
    }

    private void listHackathons() {
        List<Hackathon> hackathons = service.getAllHackathons();
        hackathons.forEach(h -> System.out.printf("ID:%d %s - %s%n",
                h.getId(), h.getName(), h.getCurrentState().getClass().getSimpleName()));
    }

    private void createHackathon() {
        // Stub: service.createHackathon(name, date...)
        System.out.println("Hackathon creato (stub)");
    }

    private void viewHackathon() {
        System.out.print("ID Hackathon: ");
        // Stub
    }

    private int getChoice() {
        try { return Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { return -1; }
    }
}
