package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.TrainerClientMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerClientMappingRepository extends JpaRepository<TrainerClientMapping, Long> {
    
    @Query("SELECT tcm FROM TrainerClientMapping tcm WHERE tcm.trainer.id = :trainerId AND tcm.isActive = true")
    List<TrainerClientMapping> findActiveClientsByTrainerId(@Param("trainerId") Long trainerId);
    
    @Query("SELECT tcm FROM TrainerClientMapping tcm WHERE tcm.client.id = :clientId AND tcm.isActive = true")
    List<TrainerClientMapping> findActiveTrainersByClientId(@Param("clientId") Long clientId);
    
    @Query("SELECT tcm FROM TrainerClientMapping tcm WHERE tcm.trainer.id = :trainerId AND tcm.client.id = :clientId AND tcm.isActive = true")
    Optional<TrainerClientMapping> findActiveMapping(@Param("trainerId") Long trainerId, @Param("clientId") Long clientId);
}
