package com.g_3.gym_ms.dto;

import lombok.Builder;

@Builder
public record LoginResponse(
        String token,
        String type,
        Long userId,
        String email,
        String name,
        String role
) {}
