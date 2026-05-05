package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.entity.Trainer;
import com.g_3.gym_ms.service.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Slf4j
public class TrainerController {
    
    private final TrainerService trainerService;
    
    /**
     * Register a new trainer
     * POST /api/trainers
     */
    @PostMapping
    public ResponseEntity<Trainer> registerTrainer(
            @RequestParam Long userId,
            @RequestParam String specialty,
            @RequestParam Double hourlyRate,
            @RequestParam(required = false) String bio) {
        
        log.info("Registering trainer for user: {}", userId);
        Trainer trainer = trainerService.registerTrainer(userId, specialty, hourlyRate, bio);
        return ResponseEntity.status(HttpStatus.CREATED).body(trainer);
    }
    
    /**
     * Get all trainers
     * GET /api/trainers
     */
    @GetMapping
    public ResponseEntity<List<Trainer>> getAllTrainers() {
        log.info("Fetching all trainers");
        List<Trainer> trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }
    
    /**
     * Get a specific trainer
     * GET /api/trainers/{trainerId}
     */
    @GetMapping("/{trainerId}")
    public ResponseEntity<Trainer> getTrainer(@PathVariable Long trainerId) {
        log.info("Fetching trainer: {}", trainerId);
        Trainer trainer = trainerService.getTrainerById(trainerId);
        return ResponseEntity.ok(trainer);
    }
    
    /**
     * Get trainer by user ID
     * GET /api/trainers/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Trainer> getTrainerByUser(@PathVariable Long userId) {
        log.info("Fetching trainer for user: {}", userId);
        Trainer trainer = trainerService.getTrainerByUserId(userId);
        return ResponseEntity.ok(trainer);
    }
    
    /**
     * Update trainer information
     * PUT /api/trainers/{trainerId}
     */
    @PutMapping("/{trainerId}")
    public ResponseEntity<Trainer> updateTrainer(
            @PathVariable Long trainerId,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) Double hourlyRate,
            @RequestParam(required = false) String bio) {
        
        log.info("Updating trainer: {}", trainerId);
        Trainer trainer = trainerService.updateTrainer(trainerId, specialty, hourlyRate, bio);
        return ResponseEntity.ok(trainer);
    }
    
    /**
     * Update trainer availability
     * PUT /api/trainers/{trainerId}/availability
     */
    @PutMapping("/{trainerId}/availability")
    public ResponseEntity<Trainer> updateAvailability(
            @PathVariable Long trainerId,
            @RequestParam Boolean isAvailable) {
        
        log.info("Updating availability for trainer: {} to {}", trainerId, isAvailable);
        Trainer trainer = trainerService.updateAvailability(trainerId, isAvailable);
        return ResponseEntity.ok(trainer);
    }
}
