package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.repository.PaymentRepository;
import com.g_3.gym_ms.repository.SubscriptionRepository;
import com.g_3.gym_ms.repository.UserRepository;
import com.g_3.gym_ms.repository.AttendanceRepository;
import com.g_3.gym_ms.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminDashboardController {
    
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentService paymentService;
    
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStatistics() {
        log.info("GET /api/admin/dashboard/statistics - Fetching dashboard statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // User statistics
        stats.put("totalUsers", userRepository.count());
        
        // Subscription statistics
        stats.put("activeSubscriptions", subscriptionRepository.countActiveSubscriptions());
        stats.put("expiredSubscriptions", subscriptionRepository.countExpiredSubscriptions());
        
        // Revenue for today
        LocalDate today = LocalDate.now();
        BigDecimal todayRevenue = paymentService.getDailyRevenue(today);
        stats.put("todayRevenue", todayRevenue != null ? todayRevenue : BigDecimal.ZERO);
        stats.put("todayTransactions", paymentService.getDailyTransactionCount(today));
        
        // Attendance count for today
        stats.put("todayCheckIns", attendanceRepository.countCheckInsForDate(today));
        
        ApiResponse<Map<String, Object>> response = ApiResponse.success(
                HttpStatus.OK.value(), 
                "Dashboard statistics fetched successfully", 
                stats
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueData(
            @RequestParam(defaultValue = "TODAY") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("GET /api/admin/dashboard/revenue - Fetching revenue data for period: {}", period);
        
        Map<String, Object> revenueData = new HashMap<>();
        LocalDate date = LocalDate.now();
        
        switch (period.toUpperCase()) {
            case "TODAY" -> {
                BigDecimal revenue = paymentService.getDailyRevenue(date);
                revenueData.put("period", "TODAY");
                revenueData.put("date", date);
                revenueData.put("revenue", revenue != null ? revenue : BigDecimal.ZERO);
                revenueData.put("transactions", paymentService.getDailyTransactionCount(date));
            }
            case "MONTH" -> {
                BigDecimal revenue = paymentService.getDailyRevenue(date);
                revenueData.put("period", "MONTH");
                revenueData.put("month", date.getMonthValue());
                revenueData.put("year", date.getYear());
                revenueData.put("revenue", revenue != null ? revenue : BigDecimal.ZERO);
            }
            case "CUSTOM" -> {
                if (startDate != null && endDate != null) {
                    BigDecimal revenue = BigDecimal.ZERO;
                    long transactionCount = 0;
                    LocalDate current = startDate;
                    while (!current.isAfter(endDate)) {
                        BigDecimal dayRevenue = paymentService.getDailyRevenue(current);
                        if (dayRevenue != null) {
                            revenue = revenue.add(dayRevenue);
                        }
                        transactionCount += paymentService.getDailyTransactionCount(current);
                        current = current.plusDays(1);
                    }
                    revenueData.put("period", "CUSTOM");
                    revenueData.put("startDate", startDate);
                    revenueData.put("endDate", endDate);
                    revenueData.put("totalRevenue", revenue);
                    revenueData.put("totalTransactions", transactionCount);
                }
            }
            default -> {
                BigDecimal revenue = paymentService.getDailyRevenue(date);
                revenueData.put("period", "TODAY");
                revenueData.put("revenue", revenue != null ? revenue : BigDecimal.ZERO);
            }
        }
        
        ApiResponse<Map<String, Object>> response = ApiResponse.success(
                HttpStatus.OK.value(), 
                "Revenue data fetched successfully", 
                revenueData
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSubscriptionMetrics() {
        log.info("GET /api/admin/dashboard/subscriptions - Fetching subscription metrics");
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("activeSubscriptions", subscriptionRepository.countActiveSubscriptions());
        metrics.put("expiredSubscriptions", subscriptionRepository.countExpiredSubscriptions());
        
        ApiResponse<Map<String, Object>> response = ApiResponse.success(
                HttpStatus.OK.value(), 
                "Subscription metrics fetched successfully", 
                metrics
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/attendance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAttendanceMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("GET /api/admin/dashboard/attendance - Fetching attendance metrics for date: {}", date);
        
        LocalDate checkDate = date != null ? date : LocalDate.now();
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("date", checkDate);
        metrics.put("checkIns", attendanceRepository.countCheckInsForDate(checkDate));
        
        ApiResponse<Map<String, Object>> response = ApiResponse.success(
                HttpStatus.OK.value(), 
                "Attendance metrics fetched successfully", 
                metrics
        );
        return ResponseEntity.ok(response);
    }
}
