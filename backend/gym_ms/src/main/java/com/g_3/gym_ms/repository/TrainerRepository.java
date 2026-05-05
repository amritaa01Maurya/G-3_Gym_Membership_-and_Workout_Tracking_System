package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}
