package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.GymClassDTO;
import com.g_3.gym_ms.service.GymClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Slf4j
public class GymClassController {
    
    private final GymClassService gymClassService;
    
    /**
     * Create a new gym class
     * POST /api/classes
     */
    @PostMapping
    public ResponseEntity<GymClassDTO> createClass(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam Long trainerId,
            @RequestParam Integer capacity,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
            @RequestParam String dayOfWeek,
            @RequestParam Double price) {
        
        log.info("Creating new class: {}", name);
        GymClassDTO classDTO = gymClassService.createClass(name, description, trainerId, 
                                                           capacity, startTime, endTime, dayOfWeek, price);
        return ResponseEntity.status(HttpStatus.CREATED).body(classDTO);
    }
    
    /**
     * Get all active gym classes
     * GET /api/classes
     */
    @GetMapping
    public ResponseEntity<List<GymClassDTO>> getAllClasses() {
        log.info("Fetching all active classes");
        List<GymClassDTO> classes = gymClassService.getAllActiveClasses();
        return ResponseEntity.ok(classes);
    }
    
    /**
     * Get gym classes by day of week
     * GET /api/classes/day/{dayOfWeek}
     */
    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<List<GymClassDTO>> getClassesByDay(@PathVariable String dayOfWeek) {
        log.info("Fetching classes for day: {}", dayOfWeek);
        List<GymClassDTO> classes = gymClassService.getClassesByDay(dayOfWeek);
        return ResponseEntity.ok(classes);
    }
    
    /**
     * Get gym classes by trainer
     * GET /api/classes/trainer/{trainerId}
     */
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<GymClassDTO>> getClassesByTrainer(@PathVariable Long trainerId) {
        log.info("Fetching classes for trainer: {}", trainerId);
        List<GymClassDTO> classes = gymClassService.getClassesByTrainer(trainerId);
        return ResponseEntity.ok(classes);
    }
    
    /**
     * Get a specific class
     * GET /api/classes/{classId}
     */
    @GetMapping("/{classId}")
    public ResponseEntity<GymClassDTO> getClass(@PathVariable Long classId) {
        log.info("Fetching class: {}", classId);
        GymClassDTO classDTO = gymClassService.getClassById(classId);
        return ResponseEntity.ok(classDTO);
    }
    
    /**
     * Update a gym class
     * PUT /api/classes/{classId}
     */
    @PutMapping("/{classId}")
    public ResponseEntity<GymClassDTO> updateClass(
            @PathVariable Long classId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
            @RequestParam(required = false) String dayOfWeek,
            @RequestParam(required = false) Double price) {
        
        log.info("Updating class: {}", classId);
        GymClassDTO classDTO = gymClassService.updateClass(classId, name, description, 
                                                           capacity, startTime, endTime, dayOfWeek, price);
        return ResponseEntity.ok(classDTO);
    }
    
    /**
     * Deactivate a gym class
     * DELETE /api/classes/{classId}
     */
    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deactivateClass(@PathVariable Long classId) {
        log.info("Deactivating class: {}", classId);
        gymClassService.deactivateClass(classId);
        return ResponseEntity.noContent().build();
    }
}
