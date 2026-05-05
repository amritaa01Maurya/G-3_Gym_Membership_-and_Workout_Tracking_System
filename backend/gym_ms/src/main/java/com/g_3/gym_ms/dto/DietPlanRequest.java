package com.g_3.gym_ms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietPlanRequest {
    @NotNull(message = "Plan name is required")
    private String name;
    
    private String description;

    private List<DietItemRequest> items;
}
