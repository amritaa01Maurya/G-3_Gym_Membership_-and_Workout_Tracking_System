package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.dto.AssignedPlansDTO;
import com.g_3.gym_ms.dto.DietPlanDTO;
import com.g_3.gym_ms.dto.WorkoutPlanDTO;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.security.CustomUserDetails;
import com.g_3.gym_ms.service.DietPlanService;
import com.g_3.gym_ms.service.WorkoutPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AssignedPlansController {

    private final WorkoutPlanService workoutPlanService;
    private final DietPlanService dietPlanService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<AssignedPlansDTO>> getMyAssignedPlans(
            @AuthenticationPrincipal CustomUserDetails principal) {

        WorkoutPlanDTO workoutPlan = null;
        DietPlanDTO dietPlan = null;

        try {
            workoutPlan = workoutPlanService.getUserAssignedPlan(principal.getId());
        } catch (ResourceNotFoundException ignored) {
            log.debug("No workout plan assigned to user {}", principal.getId());
        }

        try {
            dietPlan = dietPlanService.getUserAssignedPlan(principal.getId());
        } catch (ResourceNotFoundException ignored) {
            log.debug("No diet plan assigned to user {}", principal.getId());
        }

        AssignedPlansDTO plans = AssignedPlansDTO.builder()
                .workoutPlan(workoutPlan)
                .dietPlan(dietPlan)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Assigned plans fetched successfully", plans));
    }
}
