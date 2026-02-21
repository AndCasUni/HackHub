package it.hackhub.model.domain;

import it.hackhub.model.enums.SupportRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "support_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequest {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 32000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportRequestStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "scheduled_call_time")
    private LocalDateTime scheduledCallTime;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private User assignedMentor;
}