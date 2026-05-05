package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.dto.ProgressAnalyticsDTO;
import com.g_3.gym_ms.security.CustomUserDetails;
import com.g_3.gym_ms.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProgressAnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('MEMBER','TRAINER','ADMIN')")
    public ResponseEntity<ApiResponse<ProgressAnalyticsDTO>> getMyProgress(
            @AuthenticationPrincipal CustomUserDetails principal) {

        ProgressAnalyticsDTO progress = analyticsService.getProgressAnalytics(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Progress analytics fetched successfully", progress));
    }

    @GetMapping("/my/weight")
    @PreAuthorize("hasAnyRole('MEMBER','TRAINER','ADMIN')")
    public ResponseEntity<ApiResponse<List<ProgressAnalyticsDTO.WeightProgressPoint>>> getMyWeightProgression(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "3") int months) {

        List<ProgressAnalyticsDTO.WeightProgressPoint> points = analyticsService.getWeightProgression(principal.getId(), months);
        return ResponseEntity.ok(ApiResponse.success("Weight progression fetched successfully", points));
    }

    @GetMapping("/my/strength")
    @PreAuthorize("hasAnyRole('MEMBER','TRAINER','ADMIN')")
    public ResponseEntity<ApiResponse<List<ProgressAnalyticsDTO.StrengthProgressPoint>>> getMyStrengthProgression(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam String exerciseName,
            @RequestParam(defaultValue = "3") int months) {

        List<ProgressAnalyticsDTO.StrengthProgressPoint> points = analyticsService.getStrengthProgression(
                principal.getId(),
                exerciseName,
                months
        );
        return ResponseEntity.ok(ApiResponse.success("Strength progression fetched successfully", points));
    }

    @GetMapping("/my/frequency")
    @PreAuthorize("hasAnyRole('MEMBER','TRAINER','ADMIN')")
    public ResponseEntity<ApiResponse<ProgressAnalyticsDTO.WorkoutFrequencyStats>> getMyWorkoutFrequency(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "3") int months) {

        ProgressAnalyticsDTO.WorkoutFrequencyStats stats = analyticsService.getWorkoutFrequencyStats(principal.getId(), months);
        return ResponseEntity.ok(ApiResponse.success("Workout frequency fetched successfully", stats));
    }
}
