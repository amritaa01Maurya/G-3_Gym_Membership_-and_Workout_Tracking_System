package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    List<Booking> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status != 'CANCELLED' ORDER BY b.createdAt DESC")
    List<Booking> findActiveBookingsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.gymClass.id = :classId AND b.status IN ('PENDING', 'CONFIRMED')")
    long countActiveBookingsByClass(@Param("classId") Long classId);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.gymClass.id = :classId AND b.status IN ('PENDING', 'CONFIRMED')")
    Optional<Booking> findActiveBookingByUserAndClass(@Param("userId") Long userId, @Param("classId") Long classId);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.trainerSlot.id = :slotId AND b.status IN ('PENDING', 'CONFIRMED')")
    Optional<Booking> findActiveBookingByUserAndSlot(@Param("userId") Long userId, @Param("slotId") Long slotId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.trainerSlot.id = :slotId AND b.status IN ('PENDING', 'CONFIRMED')")
    long countActiveBookingsBySlot(@Param("slotId") Long slotId);
}
