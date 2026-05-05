package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.TrainerSlotDTO;
import com.g_3.gym_ms.entity.Trainer;
import com.g_3.gym_ms.entity.TrainerSlot;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.BookingRepository;
import com.g_3.gym_ms.repository.TrainerRepository;
import com.g_3.gym_ms.repository.TrainerSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TrainerSlotService {
    
    private final TrainerSlotRepository trainerSlotRepository;
    private final TrainerRepository trainerRepository;
    private final BookingRepository bookingRepository;
    
    /**
     * Create a new trainer slot
     */
    public TrainerSlotDTO createSlot(Long trainerId, LocalDateTime slotDateTime, 
                                     Integer durationMinutes, Double price) {
        
        if (slotDateTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Slot datetime cannot be in the past");
        }
        
        if (durationMinutes == null || durationMinutes <= 0) {
            durationMinutes = 60;
        }
        
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
        
        TrainerSlot slot = TrainerSlot.builder()
                .trainer(trainer)
                .slotDateTime(slotDateTime)
                .durationMinutes(durationMinutes)
                .price(price)
                .isAvailable(true)
                .build();
        
        TrainerSlot saved = trainerSlotRepository.save(slot);
        
        log.info("Trainer slot created for trainer: {} at {}", trainerId, slotDateTime);
        
        return convertToDTO(saved);
    }
    
    /**
     * Get available slots for a trainer
     */
    public List<TrainerSlotDTO> getAvailableSlotsByTrainer(Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
        
        List<TrainerSlot> slots = trainerSlotRepository.findAvailableSlotsByTrainer(trainerId);
        return slots.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all available slots across all trainers
     */
    public List<TrainerSlotDTO> getAllAvailableSlots() {
        List<TrainerSlot> slots = trainerSlotRepository.findAllAvailableSlots();
        return slots.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get slots for a trainer within a date range
     */
    public List<TrainerSlotDTO> getSlotsByTrainerAndRange(Long trainerId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
        
        if (startDateTime.isAfter(endDateTime)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        
        List<TrainerSlot> slots = trainerSlotRepository.findSlotsByTrainerAndDateRange(trainerId, startDateTime, endDateTime);
        return slots.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get a specific slot
     */
    public TrainerSlotDTO getSlotById(Long slotId) {
        TrainerSlot slot = trainerSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + slotId));
        return convertToDTO(slot);
    }
    
    /**
     * Update slot availability (manual update)
     */
    @Transactional
    public TrainerSlotDTO updateSlotAvailability(Long slotId, Boolean isAvailable) {
        TrainerSlot slot = trainerSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + slotId));
        
        slot.setIsAvailable(isAvailable);
        TrainerSlot updated = trainerSlotRepository.save(slot);
        return convertToDTO(updated);
    }
    
    /**
     * Delete a slot (if not booked)
     */
    @Transactional
    public void deleteSlot(Long slotId) {
        TrainerSlot slot = trainerSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + slotId));
        
        // Check if slot is booked
        long bookings = bookingRepository.countActiveBookingsBySlot(slotId);
        if (bookings > 0) {
            throw new BadRequestException("Cannot delete a booked slot");
        }
        
        trainerSlotRepository.deleteById(slotId);
        log.info("Trainer slot deleted: {}", slotId);
    }
    
    /**
     * Convert TrainerSlot entity to DTO
     */
    private TrainerSlotDTO convertToDTO(TrainerSlot slot) {
        return TrainerSlotDTO.builder()
                .id(slot.getId())
                .trainerId(slot.getTrainer().getId())
                .trainerName(slot.getTrainer().getUser().getName())
                .slotDateTime(slot.getSlotDateTime())
                .durationMinutes(slot.getDurationMinutes())
                .isAvailable(slot.getIsAvailable())
                .price(slot.getPrice())
                .build();
    }
}
