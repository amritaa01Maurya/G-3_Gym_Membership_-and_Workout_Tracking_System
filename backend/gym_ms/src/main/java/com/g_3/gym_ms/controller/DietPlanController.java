package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.dto.DietItemDTO;
import com.g_3.gym_ms.dto.DietItemRequest;
import com.g_3.gym_ms.dto.DietPlanDTO;
import com.g_3.gym_ms.dto.DietPlanRequest;
import com.g_3.gym_ms.dto.PlanAssignmentRequest;
import com.g_3.gym_ms.dto.PlanMemberAssignmentRequest;
import com.g_3.gym_ms.security.CustomUserDetails;
import com.g_3.gym_ms.service.DietPlanService;
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
@RequestMapping("/api/diet-plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class DietPlanController {

    private final DietPlanService dietPlanService;

    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<DietPlanDTO>> createDietPlan(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody DietPlanRequest request) {

        DietPlanDTO plan = dietPlanService.createDietPlan(principal.getId(), request);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Diet plan created successfully", plan),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{planId}/food")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<DietItemDTO>> addFoodToPlan(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long planId,
            @Valid @RequestBody DietItemRequest request) {

        DietItemDTO item = dietPlanService.addFoodItemToPlan(principal.getId(), planId, request);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Diet item added successfully", item),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{planId}/assign")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<String>> assignDietPlan(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long planId,
            @Valid @RequestBody PlanMemberAssignmentRequest request) {

        dietPlanService.assignPlanToUser(principal.getId(), request.getUserId(), planId);
        return ResponseEntity.ok(ApiResponse.success("Diet plan assigned successfully", null));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<String>> assignDietPlanByBody(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody PlanAssignmentRequest request) {

        dietPlanService.assignPlanToUser(principal.getId(), request.getUserId(), request.getPlanId());
        return ResponseEntity.ok(ApiResponse.success("Diet plan assigned successfully", null));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<List<DietPlanDTO>>> getMyDietPlans(
            @AuthenticationPrincipal CustomUserDetails principal) {

        List<DietPlanDTO> plans = dietPlanService.getTrainerDietPlans(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Diet plans fetched successfully", plans));
    }

    @GetMapping("/{planId}")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<ApiResponse<DietPlanDTO>> getDietPlan(@PathVariable Long planId) {
        DietPlanDTO plan = dietPlanService.getDietPlan(planId);
        return ResponseEntity.ok(ApiResponse.success("Diet plan fetched successfully", plan));
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<String>> deleteDietItem(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long itemId) {

        dietPlanService.deleteDietItem(principal.getId(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Diet item deleted successfully", null));
    }
}
