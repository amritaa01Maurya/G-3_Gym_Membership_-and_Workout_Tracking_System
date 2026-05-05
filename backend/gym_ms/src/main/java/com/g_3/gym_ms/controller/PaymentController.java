package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.dto.PaymentDTO;
import com.g_3.gym_ms.dto.PaymentRequest;
import com.g_3.gym_ms.security.CustomUserDetails;
import com.g_3.gym_ms.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    @PreAuthorize("hasRole('MEMBER') or hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<PaymentDTO>> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("POST /api/payments - Processing payment for user {} with subscription ID: {}", 
                userDetails.getId(), request.subscriptionId());
        PaymentDTO payment = paymentService.processPayment(request, userDetails.getId());
        ApiResponse<PaymentDTO> response = ApiResponse.success(HttpStatus.CREATED.value(), "Payment processed successfully", payment);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(@PathVariable Long id) {
        log.info("GET /api/payments/{} - Fetching payment", id);
        PaymentDTO payment = paymentService.getPaymentById(id);
        ApiResponse<PaymentDTO> response = ApiResponse.success(HttpStatus.OK.value(), "Payment fetched successfully", payment);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my/all")
    @PreAuthorize("hasRole('MEMBER') or hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getMyPayments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("GET /api/payments/my/all - Fetching all payments for user {}", userDetails.getId());
        List<PaymentDTO> payments = paymentService.getUserPayments(userDetails.getId());
        ApiResponse<List<PaymentDTO>> response = ApiResponse.success(HttpStatus.OK.value(), "Payments fetched successfully", payments);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my/successful")
    @PreAuthorize("hasRole('MEMBER') or hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getMySuccessfulPayments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("GET /api/payments/my/successful - Fetching successful payments for user {}", userDetails.getId());
        List<PaymentDTO> payments = paymentService.getUserSuccessfulPayments(userDetails.getId());
        ApiResponse<List<PaymentDTO>> response = ApiResponse.success(HttpStatus.OK.value(), "Payments fetched successfully", payments);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getAllPayments() {
        log.info("GET /api/payments - Fetching all payments (admin)");
        List<PaymentDTO> payments = paymentService.getAllPayments();
        ApiResponse<List<PaymentDTO>> response = ApiResponse.success(HttpStatus.OK.value(), "Payments fetched successfully", payments);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> refundPayment(@PathVariable Long id) {
        log.info("POST /api/payments/{}/refund - Processing refund for payment", id);
        paymentService.refundPayment(id);
        ApiResponse<String> response = ApiResponse.success(HttpStatus.OK.value(), "Refund processed successfully", null);
        return ResponseEntity.ok(response);
    }
}