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
public class DietItemRequest {
    @NotNull(message = "Meal type is required")
    private String mealType;
    
    @NotNull(message = "Food item is required")
    private String foodItem;
    
    @NotNull(message = "Calories is required")
    @Positive(message = "Calories must be positive")
    private Integer calories;
}
