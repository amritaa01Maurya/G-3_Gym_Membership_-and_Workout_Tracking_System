package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.Payment;
import com.g_3.gym_ms.entity.PaymentStatus;
import com.g_3.gym_ms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByUser(User user);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.user = :user AND p.status = com.g_3.gym_ms.entity.PaymentStatus.SUCCESS ORDER BY p.createdAt DESC")
    List<Payment> findSuccessfulPaymentsByUser(@Param("user") User user);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = com.g_3.gym_ms.entity.PaymentStatus.SUCCESS AND CAST(p.createdAt AS date) = :date")
    BigDecimal getDailyRevenue(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = com.g_3.gym_ms.entity.PaymentStatus.SUCCESS AND CAST(p.createdAt AS date) = :date")
    long getDailyTransactionCount(@Param("date") LocalDate date);
}
