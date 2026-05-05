package com.g_3.gym_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressAnalyticsDTO {
    private Long userId;
    private List<WeightProgressPoint> weightProgress;
    private Map<String, List<StrengthProgressPoint>> strengthProgress;
    private WorkoutFrequencyStats frequencyStats;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WeightProgressPoint {
        private String date;
        private Double weight;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StrengthProgressPoint {
        private String date;
        private Integer sets;
        private Integer reps;
        private Double weight;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkoutFrequencyStats {
        private Long totalWorkouts;
        private Long totalMinutes;
        private Double averageDurationMinutes;
        private Double weeklyAverage;
    }
}
