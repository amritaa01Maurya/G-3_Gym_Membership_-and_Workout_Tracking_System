package com.g_3.gym_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GymClassDTO {
    private Long id;
    private String name;
    private String description;
    private Long trainerId;
    private String trainerName;
    private Integer capacity;
    private Integer currentBookings;
    private Integer availableSlots;
    private LocalTime startTime;
    private LocalTime endTime;
    private String dayOfWeek;
    private Double price;
    private Boolean isActive;
}
