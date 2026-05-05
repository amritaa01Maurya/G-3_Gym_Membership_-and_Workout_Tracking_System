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
public class TrainerSlotBookingRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Trainer Slot ID is required")
    private Long trainerSlotId;
}
