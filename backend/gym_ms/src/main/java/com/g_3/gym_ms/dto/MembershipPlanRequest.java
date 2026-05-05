package com.g_3.gym_ms.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MembershipPlanRequest(
        @NotBlank(message = "Plan name is required")
        @Size(min = 3, max = 100, message = "Plan name must be between 3 and 100 characters")
        String name,
        
        @NotNull(message = "Duration in days is required")
        @Positive(message = "Duration must be greater than 0")
        Integer durationDays,
        
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,
        
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description
) {}
