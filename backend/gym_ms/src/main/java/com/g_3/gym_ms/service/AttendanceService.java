package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.AttendanceCheckInRequest;
import com.g_3.gym_ms.dto.AttendanceCheckInResponse;
import com.g_3.gym_ms.dto.AttendanceDTO;
import com.g_3.gym_ms.dto.QRCodeResponse;
import com.g_3.gym_ms.entity.Attendance;
import com.g_3.gym_ms.entity.User;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.AttendanceRepository;
import com.g_3.gym_ms.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;
    
    /**
     * Generate QR code for gym attendance on a specific date
     * Creates a unique QR code and stores attendance record
     */
    public QRCodeResponse generateQRCode(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        LocalDate today = LocalDate.now();
        
        // Check if attendance already exists for today
        if (attendanceRepository.countCheckInsForUserOnDate(userId, today) > 0) {
            throw new BadRequestException("You have already checked in today");
        }
        
        String qrData = generateQRData(userId, today);
        
        try {
            String qrCode = generateQRCodeImage(qrData);
            
            Attendance attendance = Attendance.builder()
                    .user(user)
                    .attendanceDate(today)
                    .checkInTime(LocalTime.now())
                    .qrCode(qrData)
                    .isVerified(false)
                    .build();
            
            Attendance saved = attendanceRepository.save(attendance);
            
            return QRCodeResponse.builder()
                    .qrCode(qrCode)
                    .message("QR Code generated successfully")
                    .attendanceId(saved.getId())
                    .build();
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code", e);
            throw new BadRequestException("Failed to generate QR code");
        }
    }
    
    /**
     * Check-in by scanning QR code
     * Validates QR code and prevents duplicate check-ins
     */
    public AttendanceCheckInResponse checkIn(AttendanceCheckInRequest request) {
        Long userId = request.getUserId();
        String qrCode = request.getQrCode();
        
        if (qrCode == null || qrCode.isEmpty()) {
            throw new BadRequestException("QR code cannot be empty");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        LocalDate today = LocalDate.now();
        
        // Check for duplicate check-in
        if (attendanceRepository.countCheckInsForUserOnDate(userId, today) > 0) {
            throw new BadRequestException("You have already checked in today. Duplicate check-in not allowed");
        }
        
        // Validate QR code
        Attendance attendance = attendanceRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new BadRequestException("Invalid QR code"));
        
        // Verify QR code belongs to the user
        if (!attendance.getUser().getId().equals(userId)) {
            throw new BadRequestException("QR code does not match the user");
        }
        
        // Verify date matches
        if (!attendance.getAttendanceDate().equals(today)) {
            throw new BadRequestException("QR code has expired or is invalid");
        }
        
        // Mark as verified
        attendance.setIsVerified(true);
        attendance.setCheckInTime(LocalTime.now());
        Attendance updatedAttendance = attendanceRepository.save(attendance);
        
        log.info("User {} checked in successfully on {}", userId, today);
        
        return AttendanceCheckInResponse.builder()
                .id(updatedAttendance.getId())
                .userId(userId)
                .attendanceDate(today)
                .checkInTime(updatedAttendance.getCheckInTime())
                .message("Check-in successful")
                .isSuccess(true)
                .build();
    }
    
    /**
     * Get attendance history for a user
     */
    public List<AttendanceDTO> getAttendanceHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Attendance> attendances = attendanceRepository.findByUserId(userId);
        
        return attendances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get attendance history for a user within a date range
     */
    public List<AttendanceDTO> getAttendanceHistoryByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        
        List<Attendance> attendances = attendanceRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        
        return attendances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Check out from gym
     */
    @Transactional
    public AttendanceCheckInResponse checkOut(Long userId) {
        LocalDate today = LocalDate.now();
        
        Attendance attendance = attendanceRepository.findByUserIdAndDate(userId, today)
                .orElseThrow(() -> new BadRequestException("No check-in found for today"));
        
        if (attendance.getCheckOutTime() != null) {
            throw new BadRequestException("Already checked out today");
        }
        
        attendance.setCheckOutTime(LocalTime.now());
        Attendance updated = attendanceRepository.save(attendance);
        
        return AttendanceCheckInResponse.builder()
                .id(updated.getId())
                .userId(userId)
                .attendanceDate(today)
                .checkInTime(updated.getCheckInTime())
                .message("Check-out successful")
                .isSuccess(true)
                .build();
    }
    
    /**
     * Generate QR code image as Base64
     */
    private String generateQRCodeImage(String qrData) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter()
                .encode(qrData, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
        
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        
        return Base64.getEncoder().encodeToString(pngData);
    }
    
    /**
     * Generate unique QR data containing user, date, and timestamp
     */
    private String generateQRData(Long userId, LocalDate date) {
        return String.format("GYM|USER:%d|DATE:%s|TIME:%d", userId, date, System.currentTimeMillis());
    }
    
    /**
     * Convert Attendance entity to DTO
     */
    private AttendanceDTO convertToDTO(Attendance attendance) {
        return AttendanceDTO.builder()
                .id(attendance.getId())
                .userId(attendance.getUser().getId())
                .userName(attendance.getUser().getName())
                .attendanceDate(attendance.getAttendanceDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .isVerified(attendance.getIsVerified())
                .build();
    }
}
