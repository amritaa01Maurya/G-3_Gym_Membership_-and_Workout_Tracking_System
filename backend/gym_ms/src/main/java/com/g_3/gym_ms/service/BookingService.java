package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.*;
import com.g_3.gym_ms.entity.*;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.BookingRepository;
import com.g_3.gym_ms.repository.GymClassRepository;
import com.g_3.gym_ms.repository.TrainerSlotRepository;
import com.g_3.gym_ms.repository.UserRepository;
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
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final GymClassRepository gymClassRepository;
    private final TrainerSlotRepository trainerSlotRepository;
    private final UserRepository userRepository;
    
    /**
     * Book a group class for a user
     * Prevents double booking and validates class capacity
     */
    public BookingResponse bookClass(ClassBookingRequest request) {
        Long userId = request.getUserId();
        Long classId = request.getClassId();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        GymClass gymClass = gymClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
        
        // Check if class is active
        if (!gymClass.getIsActive()) {
            throw new BadRequestException("Class is no longer available");
        }
        
        // Prevent double booking
        bookingRepository.findActiveBookingByUserAndClass(userId, classId)
                .ifPresent(b -> {
                    throw new BadRequestException("You have already booked this class");
                });
        
        // Check class capacity
        long currentBookings = bookingRepository.countActiveBookingsByClass(classId);
        if (currentBookings >= gymClass.getCapacity()) {
            throw new BadRequestException("Class is full. No more seats available");
        }
        
        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .gymClass(gymClass)
                .status(Booking.BookingStatus.CONFIRMED)
                .build();
        
        Booking savedBooking = bookingRepository.save(booking);
        
        log.info("User {} booked class {} successfully", userId, classId);
        
        return BookingResponse.builder()
                .id(savedBooking.getId())
                .userId(userId)
                .classId(classId)
                .status(savedBooking.getStatus().toString())
                .message("Class booked successfully")
                .bookedAt(savedBooking.getCreatedAt())
                .build();
    }
    
    /**
     * Book a personal trainer slot for a user
     * Prevents double booking and validates slot availability
     */
    public BookingResponse bookTrainerSlot(TrainerSlotBookingRequest request) {
        Long userId = request.getUserId();
        Long slotId = request.getTrainerSlotId();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        TrainerSlot slot = trainerSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer slot not found with id: " + slotId));
        
        // Check if slot is available
        if (!slot.getIsAvailable()) {
            throw new BadRequestException("This trainer slot is no longer available");
        }
        
        // Prevent double booking for the same slot
        bookingRepository.findActiveBookingByUserAndSlot(userId, slotId)
                .ifPresent(b -> {
                    throw new BadRequestException("You have already booked this trainer slot");
                });
        
        // Check if slot is already booked (only 1 person per slot)
        long activeBookings = bookingRepository.countActiveBookingsBySlot(slotId);
        if (activeBookings > 0) {
            throw new BadRequestException("This trainer slot is already booked");
        }
        
        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .trainerSlot(slot)
                .status(Booking.BookingStatus.CONFIRMED)
                .build();
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Mark slot as unavailable
        slot.setIsAvailable(false);
        trainerSlotRepository.save(slot);
        
        log.info("User {} booked trainer slot {} successfully", userId, slotId);
        
        return BookingResponse.builder()
                .id(savedBooking.getId())
                .userId(userId)
                .trainerSlotId(slotId)
                .status(savedBooking.getStatus().toString())
                .message("Trainer slot booked successfully")
                .bookedAt(savedBooking.getCreatedAt())
                .build();
    }
    
    /**
     * Get all bookings for a user
     */
    public List<BookingDTO> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get active bookings for a user
     */
    public List<BookingDTO> getActiveUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Booking> bookings = bookingRepository.findActiveBookingsByUserId(userId);
        
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Cancel a booking
     * Allows cancellation and frees up class/slot capacity
     */
    @Transactional
    public BookingResponse cancelBooking(BookingCancellationRequest request) {
        Long bookingId = request.getBookingId();
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        
        // Check if already cancelled
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }
        
        // Check if already completed
        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed booking");
        }
        
        // Cancel the booking
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancellationReason(request.getReason());
        Booking cancelledBooking = bookingRepository.save(booking);
        
        // If trainer slot booking, make the slot available again
        if (booking.getTrainerSlot() != null) {
            TrainerSlot slot = booking.getTrainerSlot();
            slot.setIsAvailable(true);
            trainerSlotRepository.save(slot);
        }
        
        log.info("Booking {} cancelled successfully", bookingId);
        
        return BookingResponse.builder()
                .id(cancelledBooking.getId())
                .userId(booking.getUser().getId())
                .classId(booking.getGymClass() != null ? booking.getGymClass().getId() : null)
                .trainerSlotId(booking.getTrainerSlot() != null ? booking.getTrainerSlot().getId() : null)
                .status(cancelledBooking.getStatus().toString())
                .message("Booking cancelled successfully")
                .build();
    }
    
    /**
     * Convert Booking entity to DTO
     */
    private BookingDTO convertToDTO(Booking booking) {
        Long classId = null;
        String className = null;
        LocalDateTime slotDateTime = null;
        Long trainerSlotId = null;
        
        if (booking.getGymClass() != null) {
            classId = booking.getGymClass().getId();
            className = booking.getGymClass().getName();
        }
        
        if (booking.getTrainerSlot() != null) {
            trainerSlotId = booking.getTrainerSlot().getId();
            slotDateTime = booking.getTrainerSlot().getSlotDateTime();
        }
        
        return BookingDTO.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .classId(classId)
                .className(className)
                .trainerSlotId(trainerSlotId)
                .slotDateTime(slotDateTime)
                .status(booking.getStatus().toString())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
