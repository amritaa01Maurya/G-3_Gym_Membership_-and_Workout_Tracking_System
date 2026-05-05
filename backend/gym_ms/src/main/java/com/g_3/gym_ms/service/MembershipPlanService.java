package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.MembershipPlanDTO;
import com.g_3.gym_ms.dto.MembershipPlanRequest;
import com.g_3.gym_ms.entity.MembershipPlan;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MembershipPlanService {
    
    private final MembershipPlanRepository membershipPlanRepository;
    
    public MembershipPlanDTO createPlan(MembershipPlanRequest request) {
        log.info("Creating new membership plan: {}", request.name());
        
        if (membershipPlanRepository.findByName(request.name()).isPresent()) {
            log.warn("Plan creation failed: Plan name already exists: {}", request.name());
            throw new BadRequestException("Plan with name '" + request.name() + "' already exists");
        }
        
        MembershipPlan plan = MembershipPlan.builder()
                .name(request.name())
                .durationDays(request.durationDays())
                .price(request.price())
                .description(request.description())
                .isActive(true)
                .build();
        
        MembershipPlan savedPlan = membershipPlanRepository.save(plan);
        log.info("Membership plan created successfully with ID: {}", savedPlan.getId());
        
        return convertToDTO(savedPlan);
    }
    
    @Transactional(readOnly = true)
    public MembershipPlanDTO getPlanById(Long id) {
        log.info("Fetching membership plan with ID: {}", id);
        MembershipPlan plan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with ID: " + id));
        return convertToDTO(plan);
    }
    
    public MembershipPlan getPlanEntityById(Long id) {
        log.info("Fetching membership plan entity with ID: {}", id);
        return membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<MembershipPlanDTO> getAllActivePlans() {
        log.info("Fetching all active membership plans");
        List<MembershipPlan> plans = membershipPlanRepository.findAllActivePlans();
        return plans.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<MembershipPlanDTO> getAllPlans() {
        log.info("Fetching all membership plans");
        List<MembershipPlan> plans = membershipPlanRepository.findAll();
        return plans.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    public MembershipPlanDTO updatePlan(Long id, MembershipPlanRequest request) {
        log.info("Updating membership plan with ID: {}", id);
        
        MembershipPlan plan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with ID: " + id));
        
        // Check if name is being changed and if it already exists
        if (!plan.getName().equals(request.name()) && 
                membershipPlanRepository.findByName(request.name()).isPresent()) {
            log.warn("Plan update failed: Plan name already exists: {}", request.name());
            throw new BadRequestException("Plan with name '" + request.name() + "' already exists");
        }
        
        plan.setName(request.name());
        plan.setDurationDays(request.durationDays());
        plan.setPrice(request.price());
        plan.setDescription(request.description());
        
        MembershipPlan updatedPlan = membershipPlanRepository.save(plan);
        log.info("Membership plan updated successfully with ID: {}", updatedPlan.getId());
        
        return convertToDTO(updatedPlan);
    }
    
    public void deletePlan(Long id) {
        log.info("Deleting membership plan with ID: {}", id);
        
        MembershipPlan plan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with ID: " + id));
        
        // Soft delete by marking as inactive
        plan.setIsActive(false);
        membershipPlanRepository.save(plan);
        
        log.info("Membership plan deactivated successfully with ID: {}", id);
    }
    
    public void activatePlan(Long id) {
        log.info("Activating membership plan with ID: {}", id);
        
        MembershipPlan plan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with ID: " + id));
        
        plan.setIsActive(true);
        membershipPlanRepository.save(plan);
        
        log.info("Membership plan activated successfully with ID: {}", id);
    }
    
    private MembershipPlanDTO convertToDTO(MembershipPlan plan) {
        return MembershipPlanDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .durationDays(plan.getDurationDays())
                .price(plan.getPrice())
                .description(plan.getDescription())
                .isActive(plan.getIsActive())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}
