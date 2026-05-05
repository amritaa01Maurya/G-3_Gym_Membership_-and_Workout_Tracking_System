package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.BodyMetricsDTO;
import com.g_3.gym_ms.dto.BodyMetricsRequest;
import com.g_3.gym_ms.entity.BodyMetrics;
import com.g_3.gym_ms.entity.User;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.BodyMetricsRepository;
import com.g_3.gym_ms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MetricsService {
    
    private final BodyMetricsRepository bodyMetricsRepository;
    private final UserRepository userRepository;
    
    /**
     * Add body metrics for a user
     */
    public BodyMetricsDTO addBodyMetrics(Long userId, BodyMetricsRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (request.getWeight() == null || request.getWeight() <= 0) {
            throw new BadRequestException("Weight must be positive");
        }
        
        BodyMetrics metrics = BodyMetrics.builder()
                .user(user)
                .metricDate(request.getMetricDate())
                .weight(request.getWeight())
                .bodyFatPercentage(request.getBodyFatPercentage())
                .muscleMass(request.getMuscleMass())
                .bmi(request.getBmi())
                .build();
        
        BodyMetrics saved = bodyMetricsRepository.save(metrics);
        log.info("Body metrics added for user {} on {}", userId, request.getMetricDate());
        
        return convertToDTO(saved);
    }
    
    /**
     * Get all metrics for a user
     */
    public List<BodyMetricsDTO> getUserMetrics(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<BodyMetrics> metrics = bodyMetricsRepository.findByUserId(userId);
        
        return metrics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get metrics by date range
     */
    public List<BodyMetricsDTO> getUserMetricsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        
        List<BodyMetrics> metrics = bodyMetricsRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        
        return metrics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get latest metrics for a user
     */
    public BodyMetricsDTO getLatestMetrics(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        BodyMetrics metrics = bodyMetricsRepository.findTopByUserIdOrderByMetricDateDesc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No metrics found for user"));
        
        return convertToDTO(metrics);
    }
    
    /**
     * Update body metrics
     */
    public BodyMetricsDTO updateBodyMetrics(Long userId, Long metricsId, BodyMetricsRequest request) {
        BodyMetrics metrics = bodyMetricsRepository.findById(metricsId)
                .orElseThrow(() -> new ResourceNotFoundException("Metrics not found"));
        
        if (!metrics.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access");
        }
        
        if (request.getWeight() != null && request.getWeight() > 0) {
            metrics.setWeight(request.getWeight());
        }
        if (request.getBodyFatPercentage() != null) {
            metrics.setBodyFatPercentage(request.getBodyFatPercentage());
        }
        if (request.getMuscleMass() != null) {
            metrics.setMuscleMass(request.getMuscleMass());
        }
        if (request.getBmi() != null) {
            metrics.setBmi(request.getBmi());
        }
        
        BodyMetrics updated = bodyMetricsRepository.save(metrics);
        return convertToDTO(updated);
    }
    
    /**
     * Delete metrics
     */
    public void deleteMetrics(Long userId, Long metricsId) {
        BodyMetrics metrics = bodyMetricsRepository.findById(metricsId)
                .orElseThrow(() -> new ResourceNotFoundException("Metrics not found"));
        
        if (!metrics.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access");
        }
        
        bodyMetricsRepository.deleteById(metricsId);
        log.info("Body metrics {} deleted for user {}", metricsId, userId);
    }
    
    /**
     * Convert BodyMetrics entity to DTO
     */
    private BodyMetricsDTO convertToDTO(BodyMetrics metrics) {
        return BodyMetricsDTO.builder()
                .id(metrics.getId())
                .userId(metrics.getUser().getId())
                .metricDate(metrics.getMetricDate())
                .weight(metrics.getWeight())
                .bodyFatPercentage(metrics.getBodyFatPercentage())
                .muscleMass(metrics.getMuscleMass())
                .bmi(metrics.getBmi())
                .createdAt(metrics.getCreatedAt())
                .build();
    }
}
