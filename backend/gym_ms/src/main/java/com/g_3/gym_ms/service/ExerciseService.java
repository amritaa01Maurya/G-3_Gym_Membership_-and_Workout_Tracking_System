package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.ExerciseDTO;
import com.g_3.gym_ms.dto.ExerciseRequest;
import com.g_3.gym_ms.entity.Exercise;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public ExerciseDTO createExercise(ExerciseRequest request) {
        if (exerciseRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Exercise already exists with name: " + request.getName());
        }

        Exercise exercise = Exercise.builder()
                .name(request.getName().trim())
                .category(request.getCategory().trim())
                .description(request.getDescription())
                .build();

        return convertToDTO(exerciseRepository.save(exercise));
    }

    @Transactional(readOnly = true)
    public List<ExerciseDTO> getExercises(String category) {
        List<Exercise> exercises = category == null || category.isBlank()
                ? exerciseRepository.findAllByOrderByNameAsc()
                : exerciseRepository.findByCategoryIgnoreCaseOrderByNameAsc(category);

        return exercises.stream().map(this::convertToDTO).toList();
    }

    public ExerciseDTO updateExercise(Long exerciseId, ExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));

        exerciseRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(exerciseId))
                .ifPresent(existing -> {
                    throw new BadRequestException("Exercise already exists with name: " + request.getName());
                });

        exercise.setName(request.getName().trim());
        exercise.setCategory(request.getCategory().trim());
        exercise.setDescription(request.getDescription());

        return convertToDTO(exerciseRepository.save(exercise));
    }

    public void deleteExercise(Long exerciseId) {
        if (!exerciseRepository.existsById(exerciseId)) {
            throw new ResourceNotFoundException("Exercise not found");
        }

        exerciseRepository.deleteById(exerciseId);
    }

    private ExerciseDTO convertToDTO(Exercise exercise) {
        return ExerciseDTO.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .category(exercise.getCategory())
                .description(exercise.getDescription())
                .build();
    }
}
