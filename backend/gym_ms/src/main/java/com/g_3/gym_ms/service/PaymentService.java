package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.PaymentDTO;
import com.g_3.gym_ms.dto.PaymentRequest;
import com.g_3.gym_ms.entity.Payment;
import com.g_3.gym_ms.entity.PaymentStatus;
import com.g_3.gym_ms.entity.Subscription;
import com.g_3.gym_ms.entity.User;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.PaymentRepository;
import com.g_3.gym_ms.repository.SubscriptionRepository;
import com.g_3.gym_ms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    public PaymentDTO processPayment(PaymentRequest request, Long userId) {
        log.info("Processing payment for user {} with subscription ID: {}", userId, request.subscriptionId());
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        Subscription subscription = subscriptionRepository.findById(request.subscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + request.subscriptionId()));
        
        if (!subscription.getUser().getId().equals(userId)) {
            throw new BadRequestException("Subscription does not belong to user");
        }
        
        // Create payment record
        String transactionId = generateTransactionId();
        
        Payment payment = Payment.builder()
                .user(user)
                .subscription(subscription)
                .amount(request.amount())
                .status(PaymentStatus.PENDING)
                .transactionId(transactionId)
                .description(request.description())
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Process payment (mock implementation)
        PaymentDTO paymentDTO = processPaymentMock(savedPayment);
        
        // Trigger notification
        notificationService.notifyPaymentProcessed(user, paymentDTO);
        
        log.info("Payment processed with transaction ID: {}", transactionId);
        return paymentDTO;
    }
    
    private PaymentDTO processPaymentMock(Payment payment) {
        log.info("Processing mock payment with transaction ID: {}", payment.getTransactionId());
        
        // Mock payment processing - randomly succeed or fail (for demo, let's always succeed)
        boolean paymentSuccess = true; // In production, call actual payment gateway
        
        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            log.info("Payment successful for transaction ID: {}", payment.getTransactionId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            log.warn("Payment failed for transaction ID: {}", payment.getTransactionId());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        return convertToDTO(updatedPayment);
    }
    
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        log.info("Fetching payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        return convertToDTO(payment);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentDTO> getUserPayments(Long userId) {
        log.info("Fetching all payments for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        List<Payment> payments = paymentRepository.findByUser(user);
        return payments.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<PaymentDTO> getUserSuccessfulPayments(Long userId) {
        log.info("Fetching successful payments for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        List<Payment> payments = paymentRepository.findSuccessfulPaymentsByUser(user);
        return payments.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        log.info("Fetching all payments");
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getDailyRevenue(LocalDate date) {
        log.info("Calculating daily revenue for date: {}", date);
        BigDecimal revenue = paymentRepository.getDailyRevenue(date);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public long getDailyTransactionCount(LocalDate date) {
        log.info("Getting daily transaction count for date: {}", date);
        return paymentRepository.getDailyTransactionCount(date);
    }
    
    public void refundPayment(Long paymentId) {
        log.info("Processing refund for payment ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        
        if (!payment.getStatus().equals(PaymentStatus.SUCCESS)) {
            throw new BadRequestException("Can only refund successful payments");
        }
        
        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
        
        log.info("Payment refunded successfully with ID: {}", paymentId);
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .userId(payment.getUser().getId())
                .userName(payment.getUser().getName())
                .subscriptionId(payment.getSubscription().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
