package com.g_3.gym_ms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record FreezeMembershipRequest(
        @NotNull(message = "Subscription ID is required")
        @Positive(message = "Subscription ID must be positive")
        Long subscriptionId,
        
        @NotNull(message = "Freeze start date is required")
        LocalDate freezeStartDate,
        
        @NotNull(message = "Freeze end date is required")
        LocalDate freezeEndDate
) {}
