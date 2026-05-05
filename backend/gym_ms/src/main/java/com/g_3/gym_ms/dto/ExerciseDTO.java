package com.g_3.gym_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDTO {
    private Long id;
    private String name;
    private String category;
    private String description;
}
