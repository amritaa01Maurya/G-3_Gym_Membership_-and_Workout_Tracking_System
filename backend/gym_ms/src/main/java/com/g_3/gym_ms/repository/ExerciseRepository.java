package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Exercise> findByNameIgnoreCase(String name);
    List<Exercise> findByCategoryIgnoreCaseOrderByNameAsc(String category);
    List<Exercise> findAllByOrderByNameAsc();
}
