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
public class TrainerClientMappingDTO {
    private Long id;
    private Long trainerId;
    private String trainerName;
    private Long clientId;
    private String clientName;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
