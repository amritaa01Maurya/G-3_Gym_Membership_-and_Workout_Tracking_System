package com.g_3.gym_ms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanAssignmentRequest {
    @NotNull(message = "Plan ID is required")
    private Long planId;
    
    @NotNull(message = "Plan type is required")
    private String planType; // WORKOUT or DIET
}
