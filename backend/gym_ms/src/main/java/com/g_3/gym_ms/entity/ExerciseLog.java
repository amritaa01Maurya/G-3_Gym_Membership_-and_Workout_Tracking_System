package com.g_3.gym_ms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exercise_logs", indexes = {
        @Index(name = "idx_workout_session", columnList = "workout_session_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_session_id", nullable = false)
    private WorkoutSession workoutSession;
    
    @Column(nullable = false)
    private String exerciseName;
    
    @Column(nullable = false)
    private Integer sets;
    
    @Column(nullable = false)
    private Integer reps;
    
    @Column
    private Double weight; // in kg
    
    @Column
    private Double caloriesBurned;
}
