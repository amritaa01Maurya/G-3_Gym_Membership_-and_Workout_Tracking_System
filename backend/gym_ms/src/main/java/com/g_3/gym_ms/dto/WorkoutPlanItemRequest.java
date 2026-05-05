package com.g_3.gym_ms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanItemRequest {
    @NotNull(message = "Exercise name is required")
    private String exerciseName;
    
    @NotNull(message = "Sets is required")
    @Positive(message = "Sets must be positive")
    private Integer sets;
    
    @NotNull(message = "Reps is required")
    @Positive(message = "Reps must be positive")
    private Integer reps;
    
    @NotNull(message = "Day is required")
    private String day;
}
