package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.ApiResponse;
import com.g_3.gym_ms.dto.ExerciseDTO;
import com.g_3.gym_ms.dto.ExerciseRequest;
import com.g_3.gym_ms.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<ApiResponse<ExerciseDTO>> createExercise(@Valid @RequestBody ExerciseRequest request) {
        ExerciseDTO exercise = exerciseService.createExercise(request);
        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.CREATED.value(), "Exercise created successfully", exercise),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','MEMBER')")
    public ResponseEntity<ApiResponse<List<ExerciseDTO>>> getExercises(@RequestParam(required = false) String category) {
        List<ExerciseDTO> exercises = exerciseService.getExercises(category);
        return ResponseEntity.ok(ApiResponse.success("Exercises fetched successfully", exercises));
    }

    @PutMapping("/{exerciseId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<ApiResponse<ExerciseDTO>> updateExercise(
            @PathVariable Long exerciseId,
            @Valid @RequestBody ExerciseRequest request) {

        ExerciseDTO exercise = exerciseService.updateExercise(exerciseId, request);
        return ResponseEntity.ok(ApiResponse.success("Exercise updated successfully", exercise));
    }

    @DeleteMapping("/{exerciseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteExercise(@PathVariable Long exerciseId) {
        exerciseService.deleteExercise(exerciseId);
        return ResponseEntity.ok(ApiResponse.success("Exercise deleted successfully", null));
    }
}
