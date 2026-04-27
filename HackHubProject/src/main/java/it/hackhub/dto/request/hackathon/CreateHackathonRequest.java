package it.hackhub.dto.request.hackathon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateHackathonRequest {
    public String id;
    @NotBlank public String name;
    @NotBlank public String description;
    @NotNull  public LocalDateTime startDate;
    @NotNull  public LocalDateTime endDate;
    @NotNull  public Double prizeAmount;
    @NotBlank public String organizerId;
    @NotNull  public Integer maxParticipants;
}