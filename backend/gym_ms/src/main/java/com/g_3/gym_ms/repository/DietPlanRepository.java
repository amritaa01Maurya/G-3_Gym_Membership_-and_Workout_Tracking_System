package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    
    @Query("SELECT dp FROM DietPlan dp WHERE dp.trainer.id = :trainerId AND dp.isActive = true")
    List<DietPlan> findActiveByTrainerId(@Param("trainerId") Long trainerId);
    
    @Query("SELECT dp FROM DietPlan dp WHERE dp.trainer.id = :trainerId")
    List<DietPlan> findByTrainerId(@Param("trainerId") Long trainerId);
}
