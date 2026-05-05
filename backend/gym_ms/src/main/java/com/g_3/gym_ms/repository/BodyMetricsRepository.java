package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.BodyMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BodyMetricsRepository extends JpaRepository<BodyMetrics, Long> {
    
    @Query("SELECT bm FROM BodyMetrics bm WHERE bm.user.id = :userId ORDER BY bm.metricDate DESC")
    List<BodyMetrics> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT bm FROM BodyMetrics bm WHERE bm.user.id = :userId AND bm.metricDate BETWEEN :startDate AND :endDate ORDER BY bm.metricDate ASC")
    List<BodyMetrics> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT bm FROM BodyMetrics bm WHERE bm.user.id = :userId ORDER BY bm.metricDate DESC LIMIT 1")
    Optional<BodyMetrics> findLatestByUserId(@Param("userId") Long userId);
}
