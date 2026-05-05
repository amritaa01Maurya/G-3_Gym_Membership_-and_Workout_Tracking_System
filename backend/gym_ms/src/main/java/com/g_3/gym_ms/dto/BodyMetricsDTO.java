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
public class BodyMetricsDTO {
    private Long id;
    private Long userId;
    private LocalDate metricDate;
    private Double weight;
    private Double bodyFatPercentage;
    private Double muscleMass;
    private Double bmi;
    private LocalDateTime createdAt;
}
