package it.hackhub;

import it.hackhub.model.domain.*;
import it.hackhub.model.enums.*;
import it.hackhub.observer.NotificationObserver;
import it.hackhub.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class HackHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(HackHubApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserService userService,
                                  HackathonService hackathonService,
                                  TeamService teamService,
                                  InvitationService invitationService,
                                  SubmissionService submissionService,
                                  NotificationService notificationService,
                                  EvaluationService evaluationService,
                                  SupportRequestService supportRequestService) {
        return (args) -> {
            System.out.println("=== AVVIO HACKHUB SYSTEM (SPRING BOOT) ===");

            NotificationObserver observer = new NotificationObserver(notificationService);
            hackathonService.addObserver(observer);
            invitationService.addObserver(observer);


        };
    }
}