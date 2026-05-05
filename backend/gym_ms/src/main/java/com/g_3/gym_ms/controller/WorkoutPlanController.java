package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.dto.PlanMemberAssignmentRequest;
import com.g_3.gym_ms.dto.PlanAssignmentRequest;
import com.g_3.gym_ms.dto.WorkoutPlanDTO;
import com.g_3.gym_ms.dto.WorkoutPlanItemDTO;
import com.g_3.gym_ms.dto.WorkoutPlanItemRequest;
import com.g_3.gym_ms.dto.WorkoutPlanRequest;
import com.g_3.gym_ms.security.CustomUserDetails;
import com.g_3.gym_ms.service.WorkoutPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/workout-plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<WorkoutPlanDTO>> createWorkoutPlan(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody WorkoutPlanRequest request) {

        WorkoutPlanDTO plan = workoutPlanService.createWorkoutPlan(principal.getId(), request);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Workout plan created successfully", plan),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{planId}/exercise")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<WorkoutPlanItemDTO>> addExerciseToPlan(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long planId,
            @Valid @RequestBody WorkoutPlanItemRequest request) {

        WorkoutPlanItemDTO item = workoutPlanService.addExerciseToPlan(principal.getId(), planId, request);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Workout plan item added successfully", item),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{planId}/assign")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<String>> assignWorkoutPlan(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long planId,
            @Valid @RequestBody PlanMemberAssignmentRequest request) {

        workoutPlanService.assignPlanToUser(principal.getId(), request.getUserId(), planId);
        return ResponseEntity.ok(ApiResponse.success("Workout plan assigned successfully", null));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<String>> assignWorkoutPlanByBody(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody PlanAssignmentRequest request) {

        workoutPlanService.assignPlanToUser(principal.getId(), request.getUserId(), request.getPlanId());
        return ResponseEntity.ok(ApiResponse.success("Workout plan assigned successfully", null));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<List<WorkoutPlanDTO>>> getMyWorkoutPlans(
            @AuthenticationPrincipal CustomUserDetails principal) {

        List<WorkoutPlanDTO> plans = workoutPlanService.getTrainerWorkoutPlans(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Workout plans fetched successfully", plans));
    }

    @GetMapping("/{planId}")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<ApiResponse<WorkoutPlanDTO>> getWorkoutPlan(@PathVariable Long planId) {
        WorkoutPlanDTO plan = workoutPlanService.getWorkoutPlan(planId);
        return ResponseEntity.ok(ApiResponse.success("Workout plan fetched successfully", plan));
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<String>> deletePlanItem(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long itemId) {

        workoutPlanService.deletePlanItem(principal.getId(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Workout plan item deleted successfully", null));
    }
}
