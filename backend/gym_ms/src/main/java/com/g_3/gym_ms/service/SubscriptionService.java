package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.FreezeMembershipRequest;
import com.g_3.gym_ms.dto.RenewalRequest;
import com.g_3.gym_ms.dto.SubscriptionDTO;
import com.g_3.gym_ms.dto.SubscriptionRequest;
import com.g_3.gym_ms.entity.MembershipPlan;
import com.g_3.gym_ms.entity.Subscription;
import com.g_3.gym_ms.entity.SubscriptionStatus;
import com.g_3.gym_ms.entity.User;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.SubscriptionRepository;
import com.g_3.gym_ms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final MembershipPlanService membershipPlanService;
    
    public SubscriptionDTO purchasePlan(SubscriptionRequest request, Long userId) {
        log.info("User {} attempting to purchase plan {}", userId, request.planId());
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        MembershipPlan plan = membershipPlanService.getPlanEntityById(request.planId());
        
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(plan.getDurationDays());
        
        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .status(SubscriptionStatus.ACTIVE)
                .build();
        
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("Subscription purchased successfully. Subscription ID: {}, User ID: {}, Plan ID: {}", 
                savedSubscription.getId(), userId, plan.getId());
        
        return convertToDTO(savedSubscription);
    }
    
    public SubscriptionDTO renewPlan(RenewalRequest request, Long userId) {
        log.info("User {} attempting to renew subscription {}", userId, request.subscriptionId());
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Subscription subscription = subscriptionRepository.findByIdAndUser(request.subscriptionId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found or does not belong to user"));
        
        if (!subscription.getStatus().equals(SubscriptionStatus.ACTIVE) && 
                !subscription.getStatus().equals(SubscriptionStatus.EXPIRED)) {
            throw new BadRequestException("Can only renew active or expired subscriptions");
        }
        
        MembershipPlan newPlan = membershipPlanService.getPlanEntityById(request.planId());
        
        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = newStartDate.plusDays(newPlan.getDurationDays());
        
        subscription.setStartDate(newStartDate);
        subscription.setEndDate(newEndDate);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setPlan(newPlan);
        subscription.setFreezeStartDate(null);
        subscription.setFreezeEndDate(null);
        
        Subscription renewedSubscription = subscriptionRepository.save(subscription);
        log.info("Subscription renewed successfully. Subscription ID: {}", renewedSubscription.getId());
        
        return convertToDTO(renewedSubscription);
    }
    
    public SubscriptionDTO freezeMembership(FreezeMembershipRequest request, Long userId) {
        log.info("User {} attempting to freeze subscription {} from {} to {}", 
                userId, request.subscriptionId(), request.freezeStartDate(), request.freezeEndDate());
        
        if (request.freezeStartDate().isAfter(request.freezeEndDate())) {
            throw new BadRequestException("Freeze start date must be before freeze end date");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Subscription subscription = subscriptionRepository.findByIdAndUser(request.subscriptionId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found or does not belong to user"));
        
        if (!subscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
            throw new BadRequestException("Can only freeze active subscriptions");
        }
        
        long freezeDays = java.time.temporal.ChronoUnit.DAYS.between(
                request.freezeStartDate(), request.freezeEndDate());
        
        LocalDate newEndDate = subscription.getEndDate().plusDays(freezeDays);
        
        subscription.setFreezeStartDate(request.freezeStartDate());
        subscription.setFreezeEndDate(request.freezeEndDate());
        subscription.setEndDate(newEndDate);
        subscription.setStatus(SubscriptionStatus.FROZEN);
        
        Subscription frozenSubscription = subscriptionRepository.save(subscription);
        log.info("Subscription frozen successfully. Subscription ID: {}, End date extended to: {}", 
                frozenSubscription.getId(), newEndDate);
        
        return convertToDTO(frozenSubscription);
    }
    
    public SubscriptionDTO unfreezeMembership(Long subscriptionId, Long userId) {
        log.info("User {} attempting to unfreeze subscription {}", userId, subscriptionId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Subscription subscription = subscriptionRepository.findByIdAndUser(subscriptionId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found or does not belong to user"));
        
        if (!subscription.getStatus().equals(SubscriptionStatus.FROZEN)) {
            throw new BadRequestException("Subscription is not frozen");
        }
        
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        
        Subscription unfrozenSubscription = subscriptionRepository.save(subscription);
        log.info("Subscription unfrozen successfully. Subscription ID: {}", unfrozenSubscription.getId());
        
        return convertToDTO(unfrozenSubscription);
    }
    
    @Transactional(readOnly = true)
    public SubscriptionDTO getMySubscription(Long userId) {
        log.info("Fetching subscription for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Subscription subscription = subscriptionRepository.findByUser(user).stream()
                .filter(s -> s.getStatus().equals(SubscriptionStatus.ACTIVE))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for user"));
        
        return convertToDTO(subscription);
    }
    
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getUserSubscriptions(Long userId) {
        log.info("Fetching all subscriptions for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Subscription> subscriptions = subscriptionRepository.findByUser(user);
        return subscriptions.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getAllSubscriptions() {
        log.info("Fetching all subscriptions");
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        return subscriptions.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubscriptionDTO getSubscriptionById(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        return convertToDTO(subscription);
    }
    
    @Transactional
    public void markExpiredSubscriptions() {
        log.info("Running job to mark expired subscriptions");
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions();
        
        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);
            log.info("Marked subscription {} as expired", subscription.getId());
        }
        
        log.info("Completed marking {} expired subscriptions", expiredSubscriptions.size());
    }
    
    private SubscriptionDTO convertToDTO(Subscription subscription) {
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .userId(subscription.getUser().getId())
                .userName(subscription.getUser().getName())
                .planId(subscription.getPlan().getId())
                .planName(subscription.getPlan().getName())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus().name())
                .freezeStartDate(subscription.getFreezeStartDate())
                .freezeEndDate(subscription.getFreezeEndDate())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}
