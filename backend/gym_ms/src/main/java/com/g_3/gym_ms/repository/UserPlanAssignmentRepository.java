package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.UserPlanAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPlanAssignmentRepository extends JpaRepository<UserPlanAssignment, Long> {
    
    @Query("SELECT upa FROM UserPlanAssignment upa WHERE upa.user.id = :userId AND upa.planType = :planType AND upa.isActive = true")
    Optional<UserPlanAssignment> findActiveByUserAndType(@Param("userId") Long userId, @Param("planType") String planType);
    
    @Query("SELECT upa FROM UserPlanAssignment upa WHERE upa.user.id = :userId AND upa.isActive = true")
    List<UserPlanAssignment> findActiveByUserId(@Param("userId") Long userId);

    Optional<UserPlanAssignment> findByUserIdAndPlanTypeAndPlanId(Long userId, String planType, Long planId);
}
