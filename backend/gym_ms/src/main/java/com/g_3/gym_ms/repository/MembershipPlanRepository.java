package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    
    Optional<MembershipPlan> findByName(String name);
    
    @Query("SELECT p FROM MembershipPlan p WHERE p.isActive = true ORDER BY p.durationDays ASC")
    List<MembershipPlan> findAllActivePlans();
}
