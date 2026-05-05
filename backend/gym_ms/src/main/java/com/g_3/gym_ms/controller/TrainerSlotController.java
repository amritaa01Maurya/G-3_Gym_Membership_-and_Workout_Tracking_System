package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.TrainerSlotDTO;
import com.g_3.gym_ms.service.TrainerSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trainer-slots")
@RequiredArgsConstructor
@Slf4j
public class TrainerSlotController {
    
    private final TrainerSlotService trainerSlotService;
    

    @PostMapping
    public ResponseEntity<TrainerSlotDTO> createSlot(
            @RequestParam Long trainerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime slotDateTime,
            @RequestParam(required = false) Integer durationMinutes,
            @RequestParam Double price) {
        
        log.info("Creating new trainer slot for trainer: {} at {}", trainerId, slotDateTime);
        TrainerSlotDTO slotDTO = trainerSlotService.createSlot(trainerId, slotDateTime, durationMinutes, price);
        return ResponseEntity.status(HttpStatus.CREATED).body(slotDTO);
    }

    @GetMapping("/trainer/{trainerId}/available")
    public ResponseEntity<List<TrainerSlotDTO>> getAvailableSlots(@PathVariable Long trainerId) {
        log.info("Fetching available slots for trainer: {}", trainerId);
        List<TrainerSlotDTO> slots = trainerSlotService.getAvailableSlotsByTrainer(trainerId);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/available")
    public ResponseEntity<List<TrainerSlotDTO>> getAllAvailableSlots() {
        log.info("Fetching all available trainer slots");
        List<TrainerSlotDTO> slots = trainerSlotService.getAllAvailableSlots();
        return ResponseEntity.ok(slots);
    }
    
    @GetMapping("/trainer/{trainerId}/range")
    public ResponseEntity<List<TrainerSlotDTO>> getSlotsByRange(
            @PathVariable Long trainerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        log.info("Fetching slots for trainer: {} from {} to {}", trainerId, start, end);
        List<TrainerSlotDTO> slots = trainerSlotService.getSlotsByTrainerAndRange(trainerId, start, end);
        return ResponseEntity.ok(slots);
    }
    
 
    @GetMapping("/{slotId}")
    public ResponseEntity<TrainerSlotDTO> getSlot(@PathVariable Long slotId) {
        log.info("Fetching slot: {}", slotId);
        TrainerSlotDTO slotDTO = trainerSlotService.getSlotById(slotId);
        return ResponseEntity.ok(slotDTO);
    }
    
  
    @PutMapping("/{slotId}/availability")
    public ResponseEntity<TrainerSlotDTO> updateAvailability(
            @PathVariable Long slotId,
            @RequestParam Boolean isAvailable) {
        
        log.info("Updating availability for slot: {} to {}", slotId, isAvailable);
        TrainerSlotDTO slotDTO = trainerSlotService.updateSlotAvailability(slotId, isAvailable);
        return ResponseEntity.ok(slotDTO);
    }
    
  
    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long slotId) {
        log.info("Deleting slot: {}", slotId);
        trainerSlotService.deleteSlot(slotId);
        return ResponseEntity.noContent().build();
    }
}
