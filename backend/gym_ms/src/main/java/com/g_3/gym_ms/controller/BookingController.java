package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.*;
import com.g_3.gym_ms.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    
    private final BookingService bookingService;
    
    /**
     * Book a group class
     * POST /api/booking/class
     */
    @PostMapping("/class")
    public ResponseEntity<BookingResponse> bookClass(@Valid @RequestBody ClassBookingRequest request) {
        log.info("Booking class request for user: {}, class: {}", request.getUserId(), request.getClassId());
        BookingResponse response = bookingService.bookClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Book a personal trainer slot
     * POST /api/booking/trainer
     */
    @PostMapping("/trainer")
    public ResponseEntity<BookingResponse> bookTrainerSlot(@Valid @RequestBody TrainerSlotBookingRequest request) {
        log.info("Booking trainer slot request for user: {}, slot: {}", request.getUserId(), request.getTrainerSlotId());
        BookingResponse response = bookingService.bookTrainerSlot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get all bookings for a user
     * GET /api/booking/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        log.info("Fetching all bookings for user: {}", userId);
        List<BookingDTO> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get active bookings for a user
     * GET /api/booking/user/{userId}/active
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<BookingDTO>> getActiveBookings(@PathVariable Long userId) {
        log.info("Fetching active bookings for user: {}", userId);
        List<BookingDTO> bookings = bookingService.getActiveUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Cancel a booking
     * POST /api/booking/cancel
     */
    @PostMapping("/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@Valid @RequestBody BookingCancellationRequest request) {
        log.info("Cancel booking request for booking: {}", request.getBookingId());
        BookingResponse response = bookingService.cancelBooking(request);
        return ResponseEntity.ok(response);
    }
}
