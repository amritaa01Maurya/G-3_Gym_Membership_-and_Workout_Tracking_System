package com.g_3.gym_ms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyMetricsRequest {
    @NotNull(message = "Date is required")
    private LocalDate metricDate;
    
    @NotNull(message = "Weight is required")
    private Double weight;
    
    private Double bodyFatPercentage;
    
    private Double muscleMass;
    
    private Double bmi;
}
