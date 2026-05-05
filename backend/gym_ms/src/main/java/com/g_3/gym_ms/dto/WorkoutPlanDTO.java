package com.g_3.gym_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanDTO {
    private Long id;
    private Long trainerId;
    private String trainerName;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<WorkoutPlanItemDTO> items;
}
