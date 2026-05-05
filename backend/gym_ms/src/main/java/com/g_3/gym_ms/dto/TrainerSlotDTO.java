package com.g_3.gym_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerSlotDTO {
    private Long id;
    private Long trainerId;
    private String trainerName;
    private LocalDateTime slotDateTime;
    private Integer durationMinutes;
    private Boolean isAvailable;
    private Double price;
}
