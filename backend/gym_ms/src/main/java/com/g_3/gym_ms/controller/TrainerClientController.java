package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.dto.ProgressAnalyticsDTO;
import com.g_3.gym_ms.dto.TrainerClientAssignmentRequest;
import com.g_3.gym_ms.dto.TrainerClientMappingDTO;
import com.g_3.gym_ms.dto.WorkoutSessionDTO;
import com.g_3.gym_ms.security.CustomUserDetails;
import com.g_3.gym_ms.service.AnalyticsService;
import com.g_3.gym_ms.service.TrainerClientService;
import com.g_3.gym_ms.service.WorkoutService;
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
@RequestMapping("/api/trainer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class TrainerClientController {

    private final TrainerClientService trainerClientService;
    private final AnalyticsService analyticsService;
    private final WorkoutService workoutService;

    @PostMapping("/assign-client")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<TrainerClientMappingDTO>> assignClient(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody TrainerClientAssignmentRequest request) {

        TrainerClientMappingDTO mapping = trainerClientService.assignClientToTrainer(principal.getId(), request.getClientId());
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Client assigned successfully", mapping),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/clients")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<List<TrainerClientMappingDTO>>> getClients(
            @AuthenticationPrincipal CustomUserDetails principal) {

        List<TrainerClientMappingDTO> clients = trainerClientService.getTrainerClients(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Trainer clients fetched successfully", clients));
    }

    @GetMapping("/client/{clientId}/progress")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<ApiResponse<ProgressAnalyticsDTO>> getClientProgress(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long clientId) {

        if (!"ADMIN".equals(principal.getRole())) {
            trainerClientService.ensureClientAssignedToTrainer(principal.getId(), clientId);
        }

        ProgressAnalyticsDTO progress = analyticsService.getProgressAnalytics(clientId);
        return ResponseEntity.ok(ApiResponse.success("Client progress fetched successfully", progress));
    }

    @GetMapping("/client/{clientId}/workouts")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<ApiResponse<List<WorkoutSessionDTO>>> getClientWorkouts(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long clientId) {

        if (!"ADMIN".equals(principal.getRole())) {
            trainerClientService.ensureClientAssignedToTrainer(principal.getId(), clientId);
        }

        List<WorkoutSessionDTO> workouts = workoutService.getUserWorkouts(clientId);
        return ResponseEntity.ok(ApiResponse.success("Client workouts fetched successfully", workouts));
    }

    @GetMapping("/{trainerId}/clients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TrainerClientMappingDTO>>> getTrainerClientsForAdmin(
            @PathVariable Long trainerId) {

        List<TrainerClientMappingDTO> clients = trainerClientService.getTrainerClients(trainerId);
        return ResponseEntity.ok(ApiResponse.success("Trainer clients fetched successfully", clients));
    }

    @GetMapping("/my-trainers")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<List<TrainerClientMappingDTO>>> getMyTrainers(
            @AuthenticationPrincipal CustomUserDetails principal) {

        List<TrainerClientMappingDTO> trainers = trainerClientService.getClientTrainers(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Assigned trainers fetched successfully", trainers));
    }

    @DeleteMapping("/client/{clientId}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<String>> unassignClient(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long clientId) {

        trainerClientService.unassignClient(principal.getId(), clientId);
        return ResponseEntity.ok(ApiResponse.success("Client unassigned successfully", null));
    }
}
