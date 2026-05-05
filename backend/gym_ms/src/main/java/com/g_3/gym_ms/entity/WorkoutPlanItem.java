package com.g_3.gym_ms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workout_plan_items", indexes = {
        @Index(name = "idx_workout_plan", columnList = "workout_plan_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;
    
    @Column(nullable = false)
    private String exerciseName;
    
    @Column(nullable = false)
    private Integer sets;
    
    @Column(nullable = false)
    private Integer reps;
    
    @Column(nullable = false)
    private String day; // MONDAY, TUESDAY, etc.
}
