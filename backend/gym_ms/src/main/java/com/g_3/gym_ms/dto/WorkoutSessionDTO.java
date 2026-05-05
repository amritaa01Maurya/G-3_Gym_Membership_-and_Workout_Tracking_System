package com.g_3.gym_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSessionDTO {
    private Long id;
    private Long userId;
    private LocalDate sessionDate;
    private Integer durationMinutes;
    private String notes;
    private LocalDateTime createdAt;
    private List<ExerciseLogDTO> exercises;
}
