package it.hackhub;

import it.hackhub.domain.singleton.HackHubManager;

public class HackHubApplication {
    public static void main(String[] args) {
        System.out.println("HackHub avviato - State, Observer, Singleton + SOLID");
        HackHubManager.getInstance().getMainMenu().run();
    }
}
