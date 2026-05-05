package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.WorkoutPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutPlanItemRepository extends JpaRepository<WorkoutPlanItem, Long> {
    
    @Query("SELECT wpi FROM WorkoutPlanItem wpi WHERE wpi.workoutPlan.id = :planId")
    List<WorkoutPlanItem> findByWorkoutPlanId(@Param("planId") Long planId);
    
    @Query("SELECT wpi FROM WorkoutPlanItem wpi WHERE wpi.workoutPlan.id = :planId AND wpi.day = :day")
    List<WorkoutPlanItem> findByPlanIdAndDay(@Param("planId") Long planId, @Param("day") String day);
}
