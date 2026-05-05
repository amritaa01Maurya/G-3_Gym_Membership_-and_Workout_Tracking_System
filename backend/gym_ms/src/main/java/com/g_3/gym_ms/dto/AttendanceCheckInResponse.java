package com.g_3.gym_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceCheckInResponse {
    private Long id;
    private Long userId;
    private LocalDate attendanceDate;
    private LocalTime checkInTime;
    private String message;
    private Boolean isSuccess;
}
