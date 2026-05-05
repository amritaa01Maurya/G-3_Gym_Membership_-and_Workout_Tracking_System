package com.g_3.gym_ms.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserDTO(
        Long id,
        String name,
        String email,
        String role,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
