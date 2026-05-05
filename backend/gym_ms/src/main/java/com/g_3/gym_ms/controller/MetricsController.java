package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.dto.BodyMetricsDTO;
import com.g_3.gym_ms.dto.BodyMetricsRequest;
import com.g_3.gym_ms.security.CustomUserDetails;
import com.g_3.gym_ms.service.MetricsService;
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
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MetricsController {

    private final MetricsService metricsService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<BodyMetricsDTO>> addMetrics(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody BodyMetricsRequest request) {

        Long targetUserId = resolveTargetUserId(principal, userId);
        BodyMetricsDTO metrics = metricsService.addBodyMetrics(targetUserId, request);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Metrics added successfully", metrics),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('MEMBER','TRAINER','ADMIN')")
    public ResponseEntity<ApiResponse<List<BodyMetricsDTO>>> getMyMetrics(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<BodyMetricsDTO> metrics = getMetrics(principal.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Metrics fetched successfully", metrics));
    }

    @GetMapping("/my/latest")
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<BodyMetricsDTO>> getMyLatestMetrics(@AuthenticationPrincipal CustomUserDetails principal) {
        BodyMetricsDTO metrics = metricsService.getLatestMetrics(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Latest metrics fetched successfully", metrics));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BodyMetricsDTO>>> getUserMetrics(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<BodyMetricsDTO> metrics = getMetrics(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Metrics fetched successfully", metrics));
    }

    @PutMapping("/{metricsId}")
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<BodyMetricsDTO>> updateMetrics(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long metricsId,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody BodyMetricsRequest request) {

        Long targetUserId = resolveTargetUserId(principal, userId);
        BodyMetricsDTO metrics = metricsService.updateBodyMetrics(targetUserId, metricsId, request);
        return ResponseEntity.ok(ApiResponse.success("Metrics updated successfully", metrics));
    }

    @DeleteMapping("/{metricsId}")
    @PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteMetrics(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long metricsId,
            @RequestParam(required = false) Long userId) {

        Long targetUserId = resolveTargetUserId(principal, userId);
        metricsService.deleteMetrics(targetUserId, metricsId);
        return ResponseEntity.ok(ApiResponse.success("Metrics deleted successfully", null));
    }

    private List<BodyMetricsDTO> getMetrics(Long userId, LocalDate startDate, LocalDate endDate) {
        if (startDate != null || endDate != null) {
            LocalDate from = startDate != null ? startDate : LocalDate.now().minusMonths(3);
            LocalDate to = endDate != null ? endDate : LocalDate.now();
            return metricsService.getUserMetricsByDateRange(userId, from, to);
        }

        return metricsService.getUserMetrics(userId);
    }

    private Long resolveTargetUserId(CustomUserDetails principal, Long requestedUserId) {
        if ("ADMIN".equals(principal.getRole()) && requestedUserId != null) {
            return requestedUserId;
        }

        return principal.getId();
    }
}
