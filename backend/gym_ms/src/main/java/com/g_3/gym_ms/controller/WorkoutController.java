package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.dto.ExerciseLogDTO;
import com.g_3.gym_ms.dto.ExerciseLogRequest;
import com.g_3.gym_ms.dto.WorkoutSessionDTO;
import com.g_3.gym_ms.dto.WorkoutSessionRequest;
import com.g_3.gym_ms.security.CustomUserDetails;
import com.g_3.gym_ms.service.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<WorkoutSessionDTO>> createWorkout(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody WorkoutSessionRequest request) {

        Long targetUserId = resolveTargetUserId(principal, userId);
        log.info("POST /api/workouts - Creating workout for user {}", targetUserId);
        WorkoutSessionDTO session = workoutService.createWorkoutSession(targetUserId, request);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Workout session created successfully", session),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}/exercise")
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<ExerciseLogDTO>> addExercise(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody ExerciseLogRequest request) {

        Long targetUserId = resolveTargetUserId(principal, userId);
        ExerciseLogDTO exercise = workoutService.addExerciseToSession(targetUserId, id, request);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Exercise added successfully", exercise),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}/exercises")
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<List<ExerciseLogDTO>>> addExercises(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody List<@Valid ExerciseLogRequest> requests) {

        Long targetUserId = resolveTargetUserId(principal, userId);
        List<ExerciseLogDTO> exercises = workoutService.addExercisesToSession(targetUserId, id, requests);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Exercises added successfully", exercises),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('MEMBER','TRAINER','ADMIN')")
    public ResponseEntity<ApiResponse<List<WorkoutSessionDTO>>> getMyWorkouts(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<WorkoutSessionDTO> sessions = getWorkouts(principal.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Workout history fetched successfully", sessions));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WorkoutSessionDTO>>> getUserWorkouts(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<WorkoutSessionDTO> sessions = getWorkouts(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Workout history fetched successfully", sessions));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<WorkoutSessionDTO>> getWorkout(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {

        Long targetUserId = resolveTargetUserId(principal, userId);
        WorkoutSessionDTO session = workoutService.getWorkoutSession(targetUserId, id);
        return ResponseEntity.ok(ApiResponse.success("Workout fetched successfully", session));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<WorkoutSessionDTO>> updateWorkout(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody WorkoutSessionRequest request) {

        Long targetUserId = resolveTargetUserId(principal, userId);
        WorkoutSessionDTO session = workoutService.updateWorkoutSession(targetUserId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Workout updated successfully", session));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteWorkout(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {

        Long targetUserId = resolveTargetUserId(principal, userId);
        workoutService.deleteWorkoutSession(targetUserId, id);
        return ResponseEntity.ok(ApiResponse.success("Workout deleted successfully", null));
    }

    @PutMapping("/exercise/{exerciseLogId}")
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<ExerciseLogDTO>> updateExerciseLog(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long exerciseLogId,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody ExerciseLogRequest request) {

        Long targetUserId = resolveTargetUserId(principal, userId);
        ExerciseLogDTO exercise = workoutService.updateExerciseLog(targetUserId, exerciseLogId, request);
        return ResponseEntity.ok(ApiResponse.success("Exercise log updated successfully", exercise));
    }

    @DeleteMapping("/exercise/{exerciseLogId}")
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteExerciseLog(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long exerciseLogId,
            @RequestParam(required = false) Long userId) {

        Long targetUserId = resolveTargetUserId(principal, userId);
        workoutService.deleteExerciseLog(targetUserId, exerciseLogId);
        return ResponseEntity.ok(ApiResponse.success("Exercise log deleted successfully", null));
    }

    private List<WorkoutSessionDTO> getWorkouts(Long userId, LocalDate startDate, LocalDate endDate) {
        if (startDate != null || endDate != null) {
            LocalDate from = startDate != null ? startDate : LocalDate.now().minusMonths(3);
            LocalDate to = endDate != null ? endDate : LocalDate.now();
            return workoutService.getUserWorkoutsByDateRange(userId, from, to);
        }

        return workoutService.getUserWorkouts(userId);
    }

    private Long resolveTargetUserId(CustomUserDetails principal, Long requestedUserId) {
        if ("ADMIN".equals(principal.getRole()) && requestedUserId != null) {
            return requestedUserId;
        }

        return principal.getId();
    }
}
