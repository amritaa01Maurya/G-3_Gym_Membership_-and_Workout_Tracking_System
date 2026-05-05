package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.dto.FreezeMembershipRequest;
import com.g_3.gym_ms.dto.RenewalRequest;
import com.g_3.gym_ms.dto.SubscriptionDTO;
import com.g_3.gym_ms.dto.SubscriptionRequest;
import com.g_3.gym_ms.security.CustomUserDetails;
import com.g_3.gym_ms.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    @PostMapping("/purchase")
    @PreAuthorize("hasRole('MEMBER') or hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> purchasePlan(
            @Valid @RequestBody SubscriptionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("POST /api/subscriptions/purchase - User {} purchasing plan {}", userDetails.getId(), request.planId());
        SubscriptionDTO subscription = subscriptionService.purchasePlan(request, userDetails.getId());
        ApiResponse<SubscriptionDTO> response = ApiResponse.success(HttpStatus.CREATED.value(), "Subscription purchased successfully", subscription);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/renew")
    @PreAuthorize("hasRole('MEMBER') or hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> renewPlan(
            @Valid @RequestBody RenewalRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("POST /api/subscriptions/renew - User {} renewing subscription {}", userDetails.getId(), request.subscriptionId());
        SubscriptionDTO subscription = subscriptionService.renewPlan(request, userDetails.getId());
        ApiResponse<SubscriptionDTO> response = ApiResponse.success(HttpStatus.OK.value(), "Subscription renewed successfully", subscription);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/freeze")
    @PreAuthorize("hasRole('MEMBER') or hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> freezeMembership(
            @Valid @RequestBody FreezeMembershipRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("POST /api/subscriptions/freeze - User {} freezing subscription {}", userDetails.getId(), request.subscriptionId());
        SubscriptionDTO subscription = subscriptionService.freezeMembership(request, userDetails.getId());
        ApiResponse<SubscriptionDTO> response = ApiResponse.success(HttpStatus.OK.value(), "Membership frozen successfully", subscription);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/unfreeze")
    @PreAuthorize("hasRole('MEMBER') or hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> unfreezeMembership(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("POST /api/subscriptions/{}/unfreeze - User {} unfreezing subscription", id, userDetails.getId());
        SubscriptionDTO subscription = subscriptionService.unfreezeMembership(id, userDetails.getId());
        ApiResponse<SubscriptionDTO> response = ApiResponse.success(HttpStatus.OK.value(), "Membership unfrozen successfully", subscription);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasRole('MEMBER') or hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> getMySubscription(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("GET /api/subscriptions/my - Fetching subscription for user {}", userDetails.getId());
        SubscriptionDTO subscription = subscriptionService.getMySubscription(userDetails.getId());
        ApiResponse<SubscriptionDTO> response = ApiResponse.success(HttpStatus.OK.value(), "Subscription fetched successfully", subscription);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my/all")
    @PreAuthorize("hasRole('MEMBER') or hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<List<SubscriptionDTO>>> getMySubscriptions(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("GET /api/subscriptions/my/all - Fetching all subscriptions for user {}", userDetails.getId());
        List<SubscriptionDTO> subscriptions = subscriptionService.getUserSubscriptions(userDetails.getId());
        ApiResponse<List<SubscriptionDTO>> response = ApiResponse.success(HttpStatus.OK.value(), "Subscriptions fetched successfully", subscriptions);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SubscriptionDTO>>> getAllSubscriptions() {
        log.info("GET /api/subscriptions - Fetching all subscriptions (admin)");
        List<SubscriptionDTO> subscriptions = subscriptionService.getAllSubscriptions();
        ApiResponse<List<SubscriptionDTO>> response = ApiResponse.success(HttpStatus.OK.value(), "Subscriptions fetched successfully", subscriptions);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> getSubscriptionById(@PathVariable Long id) {
        log.info("GET /api/subscriptions/{} - Fetching subscription (admin)", id);
        SubscriptionDTO subscription = subscriptionService.getSubscriptionById(id);
        ApiResponse<SubscriptionDTO> response = ApiResponse.success(HttpStatus.OK.value(), "Subscription fetched successfully", subscription);
        return ResponseEntity.ok(response);
    }
}
