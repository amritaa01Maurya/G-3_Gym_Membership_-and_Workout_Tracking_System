package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.ProgressAnalyticsDTO;
import com.g_3.gym_ms.entity.BodyMetrics;
import com.g_3.gym_ms.entity.ExerciseLog;
import com.g_3.gym_ms.entity.WorkoutSession;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.BodyMetricsRepository;
import com.g_3.gym_ms.repository.ExerciseLogRepository;
import com.g_3.gym_ms.repository.UserRepository;
import com.g_3.gym_ms.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {
    
    private final BodyMetricsRepository bodyMetricsRepository;
    private final ExerciseLogRepository exerciseLogRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;
    
    /**
     * Get weight progression data
     */
    public List<ProgressAnalyticsDTO.WeightProgressPoint> getWeightProgression(Long userId, int months) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        LocalDate startDate = LocalDate.now().minusMonths(months);
        List<BodyMetrics> metrics = bodyMetricsRepository.findByUserIdAndDateRange(userId, startDate, LocalDate.now());
        
        return metrics.stream()
                .map(m -> ProgressAnalyticsDTO.WeightProgressPoint.builder()
                        .date(m.getMetricDate().toString())
                        .weight(m.getWeight())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Get strength progression for a specific exercise
     */
    public List<ProgressAnalyticsDTO.StrengthProgressPoint> getStrengthProgression(Long userId, String exerciseName, int months) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        LocalDate startDate = LocalDate.now().minusMonths(months);
        List<ExerciseLog> exercises = exerciseLogRepository.findByUserAndExerciseName(userId, exerciseName);
        
        return exercises.stream()
                .filter(e -> e.getWorkoutSession().getSessionDate().isAfter(startDate))
                .map(e -> ProgressAnalyticsDTO.StrengthProgressPoint.builder()
                        .date(e.getWorkoutSession().getSessionDate().toString())
                        .sets(e.getSets())
                        .reps(e.getReps())
                        .weight(e.getWeight())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Get workout frequency statistics
     */
    public ProgressAnalyticsDTO.WorkoutFrequencyStats getWorkoutFrequencyStats(Long userId, int months) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        LocalDate startDate = LocalDate.now().minusMonths(months);
        List<WorkoutSession> sessions = workoutSessionRepository.findByUserIdAndDateRange(userId, startDate, LocalDate.now());
        
        long totalWorkouts = sessions.size();
        long totalMinutes = sessions.stream()
                .mapToLong(WorkoutSession::getDurationMinutes)
                .sum();
        
        double averageMinutes = totalWorkouts > 0 ? (double) totalMinutes / totalWorkouts : 0;
        
        // Calculate weekly average
        int weeks = Math.max(1, months * 4);
        double weeklyAverage = (double) totalWorkouts / weeks;
        
        return ProgressAnalyticsDTO.WorkoutFrequencyStats.builder()
                .totalWorkouts(totalWorkouts)
                .totalMinutes(totalMinutes)
                .averageDurationMinutes(averageMinutes)
                .weeklyAverage(weeklyAverage)
                .build();
    }
    
    /**
     * Get comprehensive progress analytics
     */
    public ProgressAnalyticsDTO getProgressAnalytics(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        int months = 3;
        
        List<ProgressAnalyticsDTO.WeightProgressPoint> weightProgress = getWeightProgression(userId, months);
        
        // Get most tracked exercises for strength progression
        List<String> topExercises = getTopExercises(userId, months);
        Map<String, List<ProgressAnalyticsDTO.StrengthProgressPoint>> strengthProgress = new HashMap<>();
        
        for (String exercise : topExercises) {
            strengthProgress.put(exercise, getStrengthProgression(userId, exercise, months));
        }
        
        ProgressAnalyticsDTO.WorkoutFrequencyStats frequencyStats = getWorkoutFrequencyStats(userId, months);
        
        log.info("Progress analytics generated for user {}", userId);
        
        return ProgressAnalyticsDTO.builder()
                .userId(userId)
                .weightProgress(weightProgress)
                .strengthProgress(strengthProgress)
                .frequencyStats(frequencyStats)
                .build();
    }
    
    /**
     * Get top exercises for a user
     */
    private List<String> getTopExercises(Long userId, int months) {
        LocalDate startDate = LocalDate.now().minusMonths(months);
        List<ExerciseLog> exercises = exerciseLogRepository.findByUserInDateRange(userId, startDate, LocalDate.now());
        
        return exercises.stream()
                .collect(Collectors.groupingBy(ExerciseLog::getExerciseName, Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
