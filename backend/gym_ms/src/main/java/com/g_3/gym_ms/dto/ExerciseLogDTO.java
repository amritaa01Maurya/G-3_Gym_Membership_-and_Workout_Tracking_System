package com.g_3.gym_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLogDTO {
    private Long id;
    private Long workoutSessionId;
    private String exerciseName;
    private Integer sets;
    private Integer reps;
    private Double weight;
    private Double caloriesBurned;
}
