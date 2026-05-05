package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId AND a.attendanceDate = :date")
    Optional<Attendance> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId ORDER BY a.attendanceDate DESC")
    List<Attendance> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate ORDER BY a.attendanceDate DESC")
    List<Attendance> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
    
    Optional<Attendance> findByQrCode(String qrCode);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId AND a.attendanceDate = :date")
    long countCheckInsForUserOnDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
