package com.g_3.gym_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
    private Long id;
    private Long userId;
    private Long classId;
    private String className;
    private Long trainerSlotId;
    private LocalDateTime slotDateTime;
    private String status;
    private LocalDateTime createdAt;
}
