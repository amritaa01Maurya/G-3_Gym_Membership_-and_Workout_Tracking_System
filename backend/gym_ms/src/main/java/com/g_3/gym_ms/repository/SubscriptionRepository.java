package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.Subscription;
import com.g_3.gym_ms.entity.SubscriptionStatus;
import com.g_3.gym_ms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    Optional<Subscription> findByIdAndUser(Long id, User user);
    
    List<Subscription> findByUser(User user);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = com.g_3.gym_ms.entity.SubscriptionStatus.ACTIVE AND s.endDate < CURRENT_DATE")
    List<Subscription> findExpiredSubscriptions();
    
    @Query("SELECT s FROM Subscription s WHERE s.user = :user AND s.status = com.g_3.gym_ms.entity.SubscriptionStatus.ACTIVE")
    Optional<Subscription> findActiveSubscriptionByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = com.g_3.gym_ms.entity.SubscriptionStatus.ACTIVE")
    long countActiveSubscriptions();
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = com.g_3.gym_ms.entity.SubscriptionStatus.EXPIRED")
    long countExpiredSubscriptions();
    
    @Query("SELECT s FROM Subscription s WHERE s.status = :status")
    List<Subscription> findByStatus(@Param("status") SubscriptionStatus status);
}
