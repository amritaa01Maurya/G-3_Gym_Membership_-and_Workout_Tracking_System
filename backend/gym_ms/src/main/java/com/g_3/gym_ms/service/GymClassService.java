package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.GymClassDTO;
import com.g_3.gym_ms.entity.GymClass;
import com.g_3.gym_ms.entity.Trainer;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.BookingRepository;
import com.g_3.gym_ms.repository.GymClassRepository;
import com.g_3.gym_ms.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GymClassService {
    
    private final GymClassRepository gymClassRepository;
    private final TrainerRepository trainerRepository;
    private final BookingRepository bookingRepository;
    
    /**
     * Create a new gym class
     */
    public GymClassDTO createClass(String name, String description, Long trainerId, 
                                   Integer capacity, LocalTime startTime, LocalTime endTime,
                                   String dayOfWeek, Double price) {
        
        if (capacity <= 0) {
            throw new BadRequestException("Capacity must be greater than 0");
        }
        
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new BadRequestException("Start time must be before end time");
        }
        
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
        
        GymClass gymClass = GymClass.builder()
                .name(name)
                .description(description)
                .trainer(trainer)
                .capacity(capacity)
                .startTime(startTime)
                .endTime(endTime)
                .dayOfWeek(dayOfWeek)
                .price(price)
                .isActive(true)
                .build();
        
        GymClass saved = gymClassRepository.save(gymClass);
        
        log.info("Gym class created: {} with trainer: {}", name, trainerId);
        
        return convertToDTO(saved);
    }
    
    /**
     * Get all active classes
     */
    public List<GymClassDTO> getAllActiveClasses() {
        List<GymClass> classes = gymClassRepository.findByIsActiveTrue();
        return classes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get classes by day of week
     */
    public List<GymClassDTO> getClassesByDay(String dayOfWeek) {
        List<GymClass> classes = gymClassRepository.findActiveClassesByDay(dayOfWeek);
        return classes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get classes by trainer
     */
    public List<GymClassDTO> getClassesByTrainer(Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
        
        List<GymClass> classes = gymClassRepository.findByTrainerId(trainerId);
        return classes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get a specific class
     */
    public GymClassDTO getClassById(Long classId) {
        GymClass gymClass = gymClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
        return convertToDTO(gymClass);
    }
    
    /**
     * Update a gym class
     */
    @Transactional
    public GymClassDTO updateClass(Long classId, String name, String description,
                                   Integer capacity, LocalTime startTime, LocalTime endTime,
                                   String dayOfWeek, Double price) {
        
        GymClass gymClass = gymClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
        
        if (capacity != null && capacity <= 0) {
            throw new BadRequestException("Capacity must be greater than 0");
        }
        
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new BadRequestException("Start time must be before end time");
        }
        
        if (name != null && !name.isEmpty()) {
            gymClass.setName(name);
        }
        if (description != null) {
            gymClass.setDescription(description);
        }
        if (capacity != null) {
            gymClass.setCapacity(capacity);
        }
        if (startTime != null) {
            gymClass.setStartTime(startTime);
        }
        if (endTime != null) {
            gymClass.setEndTime(endTime);
        }
        if (dayOfWeek != null && !dayOfWeek.isEmpty()) {
            gymClass.setDayOfWeek(dayOfWeek);
        }
        if (price != null) {
            gymClass.setPrice(price);
        }
        
        GymClass updated = gymClassRepository.save(gymClass);
        return convertToDTO(updated);
    }
    
    /**
     * Deactivate a class
     */
    @Transactional
    public void deactivateClass(Long classId) {
        GymClass gymClass = gymClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
        
        gymClass.setIsActive(false);
        gymClassRepository.save(gymClass);
        
        log.info("Class deactivated: {}", classId);
    }
    
    /**
     * Convert GymClass entity to DTO with booking info
     */
    private GymClassDTO convertToDTO(GymClass gymClass) {
        long currentBookings = bookingRepository.countActiveBookingsByClass(gymClass.getId());
        int availableSlots = (int) (gymClass.getCapacity() - currentBookings);
        
        return GymClassDTO.builder()
                .id(gymClass.getId())
                .name(gymClass.getName())
                .description(gymClass.getDescription())
                .trainerId(gymClass.getTrainer().getId())
                .trainerName(gymClass.getTrainer().getUser().getName())
                .capacity(gymClass.getCapacity())
                .currentBookings((int) currentBookings)
                .availableSlots(Math.max(0, availableSlots))
                .startTime(gymClass.getStartTime())
                .endTime(gymClass.getEndTime())
                .dayOfWeek(gymClass.getDayOfWeek())
                .price(gymClass.getPrice())
                .isActive(gymClass.getIsActive())
                .build();
    }
}
