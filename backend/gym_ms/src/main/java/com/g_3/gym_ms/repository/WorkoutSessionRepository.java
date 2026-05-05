package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    
    @Query("SELECT ws FROM WorkoutSession ws WHERE ws.user.id = :userId ORDER BY ws.sessionDate DESC")
    List<WorkoutSession> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ws FROM WorkoutSession ws WHERE ws.user.id = :userId AND ws.sessionDate BETWEEN :startDate AND :endDate ORDER BY ws.sessionDate DESC")
    List<WorkoutSession> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT ws FROM WorkoutSession ws WHERE ws.user.id = :userId AND ws.sessionDate = :date")
    List<WorkoutSession> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(ws) FROM WorkoutSession ws WHERE ws.user.id = :userId AND ws.sessionDate BETWEEN :startDate AND :endDate")
    Long countWorkoutsInRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
