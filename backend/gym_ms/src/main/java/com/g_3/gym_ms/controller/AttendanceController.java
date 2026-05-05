package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.AttendanceCheckInRequest;
import com.g_3.gym_ms.dto.AttendanceCheckInResponse;
import com.g_3.gym_ms.dto.AttendanceDTO;
import com.g_3.gym_ms.dto.QRCodeResponse;
import com.g_3.gym_ms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    /**
     * Generate QR code for gym check-in
     * POST /api/attendance/generate-qr/{userId}
     */
    @PostMapping("/generate-qr/{userId}")
    public ResponseEntity<QRCodeResponse> generateQRCode(@PathVariable Long userId) {
        log.info("Generating QR code for user: {}", userId);
        QRCodeResponse response = attendanceService.generateQRCode(userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check-in by scanning QR code
     * POST /api/attendance/checkin
     */
    @PostMapping("/checkin")
    public ResponseEntity<AttendanceCheckInResponse> checkIn(@RequestBody AttendanceCheckInRequest request) {
        log.info("Check-in request for user: {}", request.getUserId());
        AttendanceCheckInResponse response = attendanceService.checkIn(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Check-out from gym
     * POST /api/attendance/checkout/{userId}
     */
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<AttendanceCheckInResponse> checkOut(@PathVariable Long userId) {
        log.info("Check-out request for user: {}", userId);
        AttendanceCheckInResponse response = attendanceService.checkOut(userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get attendance history for a user
     * GET /api/attendance/history/{userId}
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceHistory(@PathVariable Long userId) {
        log.info("Fetching attendance history for user: {}", userId);
        List<AttendanceDTO> history = attendanceService.getAttendanceHistory(userId);
        return ResponseEntity.ok(history);
    }
    
    /**
     * Get attendance history for a user within a date range
     * GET /api/attendance/history/{userId}?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/history/{userId}/range")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceHistoryByRange(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Fetching attendance history for user {} from {} to {}", userId, startDate, endDate);
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        List<AttendanceDTO> history = attendanceService.getAttendanceHistoryByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(history);
    }
}
