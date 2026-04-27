package it.hackhub.model.domain;

import it.hackhub.model.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "team_invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamInvitation {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserPlayer sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private UserPlayer receiver;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}