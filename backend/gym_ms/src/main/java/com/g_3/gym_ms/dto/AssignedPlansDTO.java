package com.g_3.gym_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedPlansDTO {
    private WorkoutPlanDTO workoutPlan;
    private DietPlanDTO dietPlan;
}
