package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.GymClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GymClassRepository extends JpaRepository<GymClass, Long> {
    
    List<GymClass> findByIsActiveTrue();
    
    List<GymClass> findByTrainerId(Long trainerId);
    
    List<GymClass> findByDayOfWeek(String dayOfWeek);
    
    @Query("SELECT gc FROM GymClass gc WHERE gc.isActive = true AND gc.dayOfWeek = :dayOfWeek")
    List<GymClass> findActiveClassesByDay(@Param("dayOfWeek") String dayOfWeek);
}
