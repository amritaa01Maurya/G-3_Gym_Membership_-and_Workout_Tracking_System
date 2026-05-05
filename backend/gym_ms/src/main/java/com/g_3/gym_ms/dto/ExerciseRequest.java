package com.g_3.gym_ms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseRequest {
    @NotBlank(message = "Exercise name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;
}
