package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.TrainerSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrainerSlotRepository extends JpaRepository<TrainerSlot, Long> {
    
    @Query("SELECT ts FROM TrainerSlot ts WHERE ts.trainer.id = :trainerId AND ts.isAvailable = true ORDER BY ts.slotDateTime ASC")
    List<TrainerSlot> findAvailableSlotsByTrainer(@Param("trainerId") Long trainerId);
    
    @Query("SELECT ts FROM TrainerSlot ts WHERE ts.trainer.id = :trainerId AND ts.slotDateTime >= :startDateTime AND ts.slotDateTime <= :endDateTime ORDER BY ts.slotDateTime ASC")
    List<TrainerSlot> findSlotsByTrainerAndDateRange(@Param("trainerId") Long trainerId,
                                                       @Param("startDateTime") LocalDateTime startDateTime,
                                                       @Param("endDateTime") LocalDateTime endDateTime);
    
    @Query("SELECT ts FROM TrainerSlot ts WHERE ts.isAvailable = true ORDER BY ts.slotDateTime ASC")
    List<TrainerSlot> findAllAvailableSlots();
}
