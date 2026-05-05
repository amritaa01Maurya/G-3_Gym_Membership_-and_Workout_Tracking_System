package com.g_3.gym_ms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RenewalRequest(
        @NotNull(message = "Subscription ID is required")
        @Positive(message = "Subscription ID must be positive")
        Long subscriptionId,
        
        @NotNull(message = "Plan ID is required")
        @Positive(message = "Plan ID must be positive")
        Long planId
) {}
