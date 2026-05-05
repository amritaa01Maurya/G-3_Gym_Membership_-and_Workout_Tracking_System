package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.trainer.id = :trainerId AND wp.isActive = true")
    List<WorkoutPlan> findActiveByTrainerId(@Param("trainerId") Long trainerId);
    
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.trainer.id = :trainerId")
    List<WorkoutPlan> findByTrainerId(@Param("trainerId") Long trainerId);
}
