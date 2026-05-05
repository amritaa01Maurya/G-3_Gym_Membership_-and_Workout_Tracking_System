package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.ExerciseLogDTO;
import com.g_3.gym_ms.dto.ExerciseLogRequest;
import com.g_3.gym_ms.dto.WorkoutSessionDTO;
import com.g_3.gym_ms.dto.WorkoutSessionRequest;
import com.g_3.gym_ms.entity.ExerciseLog;
import com.g_3.gym_ms.entity.User;
import com.g_3.gym_ms.entity.WorkoutSession;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.ExerciseLogRepository;
import com.g_3.gym_ms.repository.UserRepository;
import com.g_3.gym_ms.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WorkoutService {
    
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ExerciseLogRepository exerciseLogRepository;
    private final UserRepository userRepository;
    
    /**
     * Create a new workout session for the user
     */
    public WorkoutSessionDTO createWorkoutSession(Long userId, WorkoutSessionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (request.getDurationMinutes() <= 0) {
            throw new BadRequestException("Duration must be positive");
        }
        
        WorkoutSession session = WorkoutSession.builder()
                .user(user)
                .sessionDate(request.getSessionDate())
                .durationMinutes(request.getDurationMinutes())
                .notes(request.getNotes())
                .build();
        
        WorkoutSession saved = workoutSessionRepository.save(session);
        log.info("Workout session created for user {} on {}", userId, request.getSessionDate());
        
        return convertToDTO(saved);
    }
    
    /**
     * Add exercise to a workout session
     */
    public ExerciseLogDTO addExerciseToSession(Long userId, Long sessionId, ExerciseLogRequest request) {
        WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout session not found"));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to this workout session");
        }
        
        ExerciseLog exerciseLog = ExerciseLog.builder()
                .workoutSession(session)
                .exerciseName(request.getExerciseName())
                .sets(request.getSets())
                .reps(request.getReps())
                .weight(request.getWeight())
                .caloriesBurned(request.getCaloriesBurned())
                .build();
        
        ExerciseLog saved = exerciseLogRepository.save(exerciseLog);
        log.info("Exercise {} added to workout session {}", request.getExerciseName(), sessionId);
        
        return convertExerciseToDTO(saved);
    }
    
    /**
     * Get all workout sessions for a user
     */
    public List<WorkoutSessionDTO> getUserWorkouts(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<WorkoutSession> sessions = workoutSessionRepository.findByUserId(userId);
        
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get workout sessions by date range
     */
    public List<WorkoutSessionDTO> getUserWorkoutsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        
        List<WorkoutSession> sessions = workoutSessionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get a specific workout session
     */
    public WorkoutSessionDTO getWorkoutSession(Long userId, Long sessionId) {
        WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout session not found"));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access");
        }
        
        return convertToDTO(session);
    }
    
    /**
     * Update a workout session
     */
    public WorkoutSessionDTO updateWorkoutSession(Long userId, Long sessionId, WorkoutSessionRequest request) {
        WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout session not found"));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access");
        }
        
        if (request.getDurationMinutes() != null && request.getDurationMinutes() > 0) {
            session.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getNotes() != null) {
            session.setNotes(request.getNotes());
        }
        
        WorkoutSession updated = workoutSessionRepository.save(session);
        return convertToDTO(updated);
    }
    
    /**
     * Delete a workout session
     */
    public void deleteWorkoutSession(Long userId, Long sessionId) {
        WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout session not found"));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access");
        }
        
        workoutSessionRepository.deleteById(sessionId);
        log.info("Workout session {} deleted for user {}", sessionId, userId);
    }
    
    /**
     * Delete an exercise from a session
     */
    public void deleteExerciseLog(Long userId, Long exerciseLogId) {
        ExerciseLog exerciseLog = exerciseLogRepository.findById(exerciseLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise log not found"));
        
        if (!exerciseLog.getWorkoutSession().getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access");
        }
        
        exerciseLogRepository.deleteById(exerciseLogId);
        log.info("Exercise log {} deleted", exerciseLogId);
    }
    
    /**
     * Convert WorkoutSession entity to DTO with exercises
     */
    private WorkoutSessionDTO convertToDTO(WorkoutSession session) {
        List<ExerciseLog> exercises = exerciseLogRepository.findByWorkoutSessionId(session.getId());
        List<ExerciseLogDTO> exerciseDTOs = exercises.stream()
                .map(this::convertExerciseToDTO)
                .collect(Collectors.toList());
        
        return WorkoutSessionDTO.builder()
                .id(session.getId())
                .userId(session.getUser().getId())
                .sessionDate(session.getSessionDate())
                .durationMinutes(session.getDurationMinutes())
                .notes(session.getNotes())
                .createdAt(session.getCreatedAt())
                .exercises(exerciseDTOs)
                .build();
    }
    
    /**
     * Convert ExerciseLog entity to DTO
     */
    private ExerciseLogDTO convertExerciseToDTO(ExerciseLog exerciseLog) {
        return ExerciseLogDTO.builder()
                .id(exerciseLog.getId())
                .workoutSessionId(exerciseLog.getWorkoutSession().getId())
                .exerciseName(exerciseLog.getExerciseName())
                .sets(exerciseLog.getSets())
                .reps(exerciseLog.getReps())
                .weight(exerciseLog.getWeight())
                .caloriesBurned(exerciseLog.getCaloriesBurned())
                .build();
    }
}
