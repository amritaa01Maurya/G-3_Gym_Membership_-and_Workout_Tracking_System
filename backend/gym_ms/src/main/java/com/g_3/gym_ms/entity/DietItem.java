package com.g_3.gym_ms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diet_items", indexes = {
        @Index(name = "idx_diet_plan", columnList = "diet_plan_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_plan_id", nullable = false)
    private DietPlan dietPlan;
    
    @Column(nullable = false)
    private String mealType; // Breakfast, Lunch, Dinner, Snack
    
    @Column(nullable = false)
    private String foodItem;
    
    @Column(nullable = false)
    private Integer calories;
}
