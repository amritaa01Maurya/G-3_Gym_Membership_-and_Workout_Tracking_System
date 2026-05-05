package com.g_3.gym_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long planId;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDate freezeStartDate;
    private LocalDate freezeEndDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
