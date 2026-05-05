package com.g_3.gym_ms.service;

import com.g_3.gym_ms.entity.Trainer;
import com.g_3.gym_ms.entity.User;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.TrainerRepository;
import com.g_3.gym_ms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TrainerService {
    
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    
    /**
     * Register a trainer
     */
    public Trainer registerTrainer(Long userId, String specialty, Double hourlyRate, String bio) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Check if user is already a trainer
        if (trainerRepository.existsByUserId(userId)) {
            throw new BadRequestException("User is already registered as a trainer");
        }
        
        if (hourlyRate == null || hourlyRate <= 0) {
            throw new BadRequestException("Hourly rate must be greater than 0");
        }
        
        Trainer trainer = Trainer.builder()
                .user(user)
                .specialty(specialty)
                .hourlyRate(hourlyRate)
                .bio(bio)
                .isAvailable(true)
                .build();
        
        Trainer saved = trainerRepository.save(trainer);
        
        log.info("Trainer registered for user: {}", userId);
        
        return saved;
    }
    
    /**
     * Get trainer by ID
     */
    public Trainer getTrainerById(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
    }
    
    /**
     * Get trainer by user ID
     */
    public Trainer getTrainerByUserId(Long userId) {
        return trainerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found for user: " + userId));
    }
    
    /**
     * Get all trainers
     */
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }
    
    /**
     * Update trainer information
     */
    @Transactional
    public Trainer updateTrainer(Long trainerId, String specialty, Double hourlyRate, String bio) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
        
        if (specialty != null && !specialty.isEmpty()) {
            trainer.setSpecialty(specialty);
        }
        if (hourlyRate != null && hourlyRate > 0) {
            trainer.setHourlyRate(hourlyRate);
        }
        if (bio != null) {
            trainer.setBio(bio);
        }
        
        Trainer updated = trainerRepository.save(trainer);
        log.info("Trainer updated: {}", trainerId);
        return updated;
    }
    
    /**
     * Update trainer availability status
     */
    @Transactional
    public Trainer updateAvailability(Long trainerId, Boolean isAvailable) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
        
        trainer.setIsAvailable(isAvailable);
        Trainer updated = trainerRepository.save(trainer);
        log.info("Trainer availability updated: {} - {}", trainerId, isAvailable);
        return updated;
    }
}
