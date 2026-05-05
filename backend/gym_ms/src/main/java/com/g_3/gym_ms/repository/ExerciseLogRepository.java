package com.g_3.gym_ms.repository;

import com.g_3.gym_ms.entity.ExerciseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {
    
    @Query("SELECT el FROM ExerciseLog el WHERE el.workoutSession.id = :sessionId")
    List<ExerciseLog> findByWorkoutSessionId(@Param("sessionId") Long sessionId);
    
    @Query("SELECT DISTINCT el.exerciseName FROM ExerciseLog el WHERE el.workoutSession.user.id = :userId ORDER BY el.exerciseName")
    List<String> findDistinctExercisesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT el FROM ExerciseLog el WHERE el.workoutSession.user.id = :userId AND el.exerciseName = :exerciseName ORDER BY el.workoutSession.sessionDate DESC")
    List<ExerciseLog> findExerciseProgressByUserAndName(@Param("userId") Long userId, @Param("exerciseName") String exerciseName);
    
    @Query("SELECT el FROM ExerciseLog el WHERE el.workoutSession.user.id = :userId AND el.exerciseName = :exerciseName ORDER BY el.workoutSession.sessionDate DESC")
    List<ExerciseLog> findByUserAndExerciseName(@Param("userId") Long userId, @Param("exerciseName") String exerciseName);
    
    @Query("SELECT el FROM ExerciseLog el WHERE el.workoutSession.user.id = :userId AND el.workoutSession.sessionDate BETWEEN :startDate AND :endDate ORDER BY el.workoutSession.sessionDate ASC")
    List<ExerciseLog> findByUserInDateRange(@Param("userId") Long userId, @Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);
}
