package com.g_3.gym_ms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "Subscription ID is required")
        @Positive(message = "Subscription ID must be positive")
        Long subscriptionId,
        
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        BigDecimal amount,
        
        String description
) {}
