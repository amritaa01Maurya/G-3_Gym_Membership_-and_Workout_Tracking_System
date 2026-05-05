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
public class BookingCancellationRequest {
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    private String reason;
}
