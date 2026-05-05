package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.DietItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietItemRepository extends JpaRepository<DietItem, Long> {
    
    @Query("SELECT di FROM DietItem di WHERE di.dietPlan.id = :planId")
    List<DietItem> findByDietPlanId(@Param("planId") Long planId);
    
    @Query("SELECT di FROM DietItem di WHERE di.dietPlan.id = :planId AND di.mealType = :mealType")
    List<DietItem> findByPlanIdAndMealType(@Param("planId") Long planId, @Param("mealType") String mealType);
}
