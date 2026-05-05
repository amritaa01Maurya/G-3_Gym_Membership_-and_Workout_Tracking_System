package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.dto.MembershipPlanDTO;
import com.g_3.gym_ms.dto.MembershipPlanRequest;
import com.g_3.gym_ms.service.MembershipPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MembershipPlanController {
    
    private final MembershipPlanService membershipPlanService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MembershipPlanDTO>> createPlan(@Valid @RequestBody MembershipPlanRequest request) {
        log.info("POST /api/plans - Creating new membership plan: {}", request.name());
        MembershipPlanDTO plan = membershipPlanService.createPlan(request);
        ApiResponse<MembershipPlanDTO> response = ApiResponse.success(HttpStatus.CREATED.value(), "Plan created successfully", plan);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<MembershipPlanDTO>>> getAllActivePlans() {
        log.info("GET /api/plans - Fetching all active membership plans");
        List<MembershipPlanDTO> plans = membershipPlanService.getAllActivePlans();
        ApiResponse<List<MembershipPlanDTO>> response = ApiResponse.success(HttpStatus.OK.value(), "Plans fetched successfully", plans);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<MembershipPlanDTO>> getPlanById(@PathVariable Long id) {
        log.info("GET /api/plans/{} - Fetching membership plan", id);
        MembershipPlanDTO plan = membershipPlanService.getPlanById(id);
        ApiResponse<MembershipPlanDTO> response = ApiResponse.success(HttpStatus.OK.value(), "Plan fetched successfully", plan);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MembershipPlanDTO>>> getAllPlans() {
        log.info("GET /api/plans/admin/all - Fetching all membership plans (admin)");
        List<MembershipPlanDTO> plans = membershipPlanService.getAllPlans();
        ApiResponse<List<MembershipPlanDTO>> response = ApiResponse.success(HttpStatus.OK.value(), "Plans fetched successfully", plans);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MembershipPlanDTO>> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody MembershipPlanRequest request) {
        log.info("PUT /api/plans/{} - Updating membership plan", id);
        MembershipPlanDTO plan = membershipPlanService.updatePlan(id, request);
        ApiResponse<MembershipPlanDTO> response = ApiResponse.success(HttpStatus.OK.value(), "Plan updated successfully", plan);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deletePlan(@PathVariable Long id) {
        log.info("DELETE /api/plans/{} - Deleting membership plan", id);
        membershipPlanService.deletePlan(id);
        ApiResponse<String> response = ApiResponse.success(HttpStatus.OK.value(), "Plan deleted successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> activatePlan(@PathVariable Long id) {
        log.info("POST /api/plans/{}/activate - Activating membership plan", id);
        membershipPlanService.activatePlan(id);
        ApiResponse<String> response = ApiResponse.success(HttpStatus.OK.value(), "Plan activated successfully", null);
        return ResponseEntity.ok(response);
    }
}
