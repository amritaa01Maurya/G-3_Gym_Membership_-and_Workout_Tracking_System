package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.DietItemDTO;
import com.g_3.gym_ms.dto.DietItemRequest;
import com.g_3.gym_ms.dto.DietPlanDTO;
import com.g_3.gym_ms.dto.DietPlanRequest;
import com.g_3.gym_ms.entity.DietItem;
import com.g_3.gym_ms.entity.DietPlan;
import com.g_3.gym_ms.entity.User;
import com.g_3.gym_ms.entity.UserPlanAssignment;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.DietItemRepository;
import com.g_3.gym_ms.repository.DietPlanRepository;
import com.g_3.gym_ms.repository.TrainerClientMappingRepository;
import com.g_3.gym_ms.repository.UserRepository;
import com.g_3.gym_ms.repository.UserPlanAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DietPlanService {
    
    private final DietPlanRepository dietPlanRepository;
    private final DietItemRepository dietItemRepository;
    private final UserRepository userRepository;
    private final UserPlanAssignmentRepository planAssignmentRepository;
    private final TrainerClientMappingRepository trainerClientMappingRepository;
    
    /**
     * Create a new diet plan
     */
    public DietPlanDTO createDietPlan(Long trainerId, DietPlanRequest request) {
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        
        DietPlan plan = DietPlan.builder()
                .trainer(trainer)
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .build();
        
        DietPlan saved = dietPlanRepository.save(plan);
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            request.getItems().forEach(itemRequest -> saveDietItem(saved, itemRequest));
        }

        log.info("Diet plan created by trainer {}", trainerId);
        
        return convertToDTO(saved);
    }
    
    /**
     * Add food item to a diet plan
     */
    public DietItemDTO addFoodItemToPlan(Long trainerId, Long planId, DietItemRequest request) {
        DietPlan plan = dietPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
        
        if (!plan.getTrainer().getId().equals(trainerId)) {
            throw new BadRequestException("Unauthorized access to this plan");
        }
        
        DietItem saved = saveDietItem(plan, request);
        log.info("Food item added to plan {}", planId);
        
        return convertItemToDTO(saved);
    }
    
    /**
     * Get diet plan details
     */
    public DietPlanDTO getDietPlan(Long planId) {
        DietPlan plan = dietPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
        
        return convertToDTO(plan);
    }
    
    /**
     * Get all diet plans created by a trainer
     */
    public List<DietPlanDTO> getTrainerDietPlans(Long trainerId) {
        userRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        
        List<DietPlan> plans = dietPlanRepository.findActiveByTrainerId(trainerId);
        
        return plans.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Assign diet plan to a user
     */
    public void assignPlanToUser(Long trainerId, Long userId, Long planId) {
        DietPlan plan = dietPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
        
        if (!plan.getTrainer().getId().equals(trainerId)) {
            throw new BadRequestException("You can only assign your own plans");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        trainerClientMappingRepository.findActiveMapping(trainerId, userId)
                .orElseThrow(() -> new BadRequestException("You can only assign plans to your active clients"));
        
        // Remove old assignment
        planAssignmentRepository.findActiveByUserAndType(userId, "DIET").ifPresent(a -> {
            if (a.getPlanId().equals(planId)) {
                return;
            }
            a.setIsActive(false);
            planAssignmentRepository.save(a);
        });

        planAssignmentRepository.findActiveByUserAndType(userId, "DIET")
                .filter(a -> a.getPlanId().equals(planId))
                .ifPresent(a -> {
                    throw new BadRequestException("Diet plan is already assigned to this user");
                });

        UserPlanAssignment assignment = planAssignmentRepository
                .findByUserIdAndPlanTypeAndPlanId(userId, "DIET", planId)
                .orElseGet(() -> UserPlanAssignment.builder()
                        .user(user)
                        .planType("DIET")
                        .planId(planId)
                        .build());

        assignment.setIsActive(true);
        
        planAssignmentRepository.save(assignment);
        log.info("Diet plan {} assigned to user {}", planId, userId);
    }
    
    /**
     * Get assigned diet plan for a user
     */
    public DietPlanDTO getUserAssignedPlan(Long userId) {
        UserPlanAssignment assignment = planAssignmentRepository.findActiveByUserAndType(userId, "DIET")
                .orElseThrow(() -> new ResourceNotFoundException("No diet plan assigned"));
        
        DietPlan plan = dietPlanRepository.findById(assignment.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
        
        return convertToDTO(plan);
    }
    
    /**
     * Delete a diet item
     */
    public void deleteDietItem(Long trainerId, Long itemId) {
        DietItem item = dietItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Diet item not found"));
        
        if (!item.getDietPlan().getTrainer().getId().equals(trainerId)) {
            throw new BadRequestException("Unauthorized access");
        }
        
        dietItemRepository.deleteById(itemId);
        log.info("Diet item {} deleted", itemId);
    }
    
    /**
     * Convert DietPlan to DTO with items
     */
    private DietPlanDTO convertToDTO(DietPlan plan) {
        List<DietItem> items = dietItemRepository.findByDietPlanId(plan.getId());
        List<DietItemDTO> itemDTOs = items.stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
        
        return DietPlanDTO.builder()
                .id(plan.getId())
                .trainerId(plan.getTrainer().getId())
                .trainerName(plan.getTrainer().getName())
                .name(plan.getName())
                .description(plan.getDescription())
                .isActive(plan.getIsActive())
                .createdAt(plan.getCreatedAt())
                .items(itemDTOs)
                .build();
    }
    
    /**
     * Convert DietItem to DTO
     */
    private DietItemDTO convertItemToDTO(DietItem item) {
        return DietItemDTO.builder()
                .id(item.getId())
                .dietPlanId(item.getDietPlan().getId())
                .mealType(item.getMealType())
                .foodItem(item.getFoodItem())
                .calories(item.getCalories())
                .build();
    }

    private DietItem saveDietItem(DietPlan plan, DietItemRequest request) {
        DietItem item = DietItem.builder()
                .dietPlan(plan)
                .mealType(request.getMealType())
                .foodItem(request.getFoodItem())
                .calories(request.getCalories())
                .build();

        return dietItemRepository.save(item);
    }
}
