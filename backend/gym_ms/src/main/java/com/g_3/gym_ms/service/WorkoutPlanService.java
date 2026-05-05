package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.*;
import com.g_3.gym_ms.entity.*;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.*;
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
public class WorkoutPlanService {
    
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutPlanItemRepository planItemRepository;
    private final UserRepository userRepository;
    private final UserPlanAssignmentRepository planAssignmentRepository;
    private final TrainerClientMappingRepository trainerClientMappingRepository;
    
    /**
     * Create a new workout plan
     */
    public WorkoutPlanDTO createWorkoutPlan(Long trainerId, WorkoutPlanRequest request) {
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        
        WorkoutPlan plan = WorkoutPlan.builder()
                .trainer(trainer)
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .build();
        
        WorkoutPlan saved = workoutPlanRepository.save(plan);
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            request.getItems().forEach(itemRequest -> savePlanItem(saved, itemRequest));
        }

        log.info("Workout plan created by trainer {}", trainerId);
        
        return convertToDTO(saved);
    }
    
    /**
     * Add exercise to a workout plan
     */
    public WorkoutPlanItemDTO addExerciseToPlan(Long trainerId, Long planId, WorkoutPlanItemRequest request) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
        
        if (!plan.getTrainer().getId().equals(trainerId)) {
            throw new BadRequestException("Unauthorized access to this plan");
        }
        
        WorkoutPlanItem saved = savePlanItem(plan, request);
        log.info("Exercise added to plan {}", planId);
        
        return convertItemToDTO(saved);
    }
    
    /**
     * Get workout plan details
     */
    public WorkoutPlanDTO getWorkoutPlan(Long planId) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
        
        return convertToDTO(plan);
    }
    
    /**
     * Get all plans created by a trainer
     */
    public List<WorkoutPlanDTO> getTrainerWorkoutPlans(Long trainerId) {
        userRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        
        List<WorkoutPlan> plans = workoutPlanRepository.findActiveByTrainerId(trainerId);
        
        return plans.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Assign plan to a user
     */
    public void assignPlanToUser(Long trainerId, Long userId, Long planId) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
        
        if (!plan.getTrainer().getId().equals(trainerId)) {
            throw new BadRequestException("You can only assign your own plans");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        trainerClientMappingRepository.findActiveMapping(trainerId, userId)
                .orElseThrow(() -> new BadRequestException("You can only assign plans to your active clients"));
        
        // Remove old assignment
        planAssignmentRepository.findActiveByUserAndType(userId, "WORKOUT").ifPresent(a -> {
            if (a.getPlanId().equals(planId)) {
                return;
            }
            a.setIsActive(false);
            planAssignmentRepository.save(a);
        });

        planAssignmentRepository.findActiveByUserAndType(userId, "WORKOUT")
                .filter(a -> a.getPlanId().equals(planId))
                .ifPresent(a -> {
                    throw new BadRequestException("Workout plan is already assigned to this user");
                });

        UserPlanAssignment assignment = planAssignmentRepository
                .findByUserIdAndPlanTypeAndPlanId(userId, "WORKOUT", planId)
                .orElseGet(() -> UserPlanAssignment.builder()
                        .user(user)
                        .planType("WORKOUT")
                        .planId(planId)
                        .build());

        assignment.setIsActive(true);
        
        planAssignmentRepository.save(assignment);
        log.info("Workout plan {} assigned to user {}", planId, userId);
    }
    
    /**
     * Get assigned plan for a user
     */
    public WorkoutPlanDTO getUserAssignedPlan(Long userId) {
        UserPlanAssignment assignment = planAssignmentRepository.findActiveByUserAndType(userId, "WORKOUT")
                .orElseThrow(() -> new ResourceNotFoundException("No workout plan assigned"));
        
        WorkoutPlan plan = workoutPlanRepository.findById(assignment.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
        
        return convertToDTO(plan);
    }
    
    /**
     * Delete a plan item
     */
    public void deletePlanItem(Long trainerId, Long itemId) {
        WorkoutPlanItem item = planItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan item not found"));
        
        if (!item.getWorkoutPlan().getTrainer().getId().equals(trainerId)) {
            throw new BadRequestException("Unauthorized access");
        }
        
        planItemRepository.deleteById(itemId);
        log.info("Plan item {} deleted", itemId);
    }
    
    /**
     * Convert WorkoutPlan to DTO with items
     */
    private WorkoutPlanDTO convertToDTO(WorkoutPlan plan) {
        List<WorkoutPlanItem> items = planItemRepository.findByWorkoutPlanId(plan.getId());
        List<WorkoutPlanItemDTO> itemDTOs = items.stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
        
        return WorkoutPlanDTO.builder()
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
     * Convert WorkoutPlanItem to DTO
     */
    private WorkoutPlanItemDTO convertItemToDTO(WorkoutPlanItem item) {
        return WorkoutPlanItemDTO.builder()
                .id(item.getId())
                .workoutPlanId(item.getWorkoutPlan().getId())
                .exerciseName(item.getExerciseName())
                .sets(item.getSets())
                .reps(item.getReps())
                .day(item.getDay())
                .build();
    }

    private WorkoutPlanItem savePlanItem(WorkoutPlan plan, WorkoutPlanItemRequest request) {
        WorkoutPlanItem item = WorkoutPlanItem.builder()
                .workoutPlan(plan)
                .exerciseName(request.getExerciseName())
                .sets(request.getSets())
                .reps(request.getReps())
                .day(request.getDay())
                .build();

        return planItemRepository.save(item);
    }
}
