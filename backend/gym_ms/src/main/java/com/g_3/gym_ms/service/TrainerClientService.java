package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.TrainerClientMappingDTO;
import com.g_3.gym_ms.entity.TrainerClientMapping;
import com.g_3.gym_ms.entity.User;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.TrainerClientMappingRepository;
import com.g_3.gym_ms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrainerClientService {
    
    private final TrainerClientMappingRepository mappingRepository;
    private final UserRepository userRepository;
    
    /**
     * Assign a client to a trainer
     */
    public TrainerClientMappingDTO assignClientToTrainer(Long trainerId, Long clientId) {
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        
        // Check if already assigned
        mappingRepository.findActiveMapping(trainerId, clientId).ifPresent(m -> {
            throw new BadRequestException("Client is already assigned to this trainer");
        });
        
        TrainerClientMapping mapping = TrainerClientMapping.builder()
                .trainer(trainer)
                .client(client)
                .isActive(true)
                .build();
        
        TrainerClientMapping saved = mappingRepository.save(mapping);
        log.info("Client {} assigned to trainer {}", clientId, trainerId);
        
        return convertToDTO(saved);
    }
    
    /**
     * Get all active clients for a trainer
     */
    public List<TrainerClientMappingDTO> getTrainerClients(Long trainerId) {
        userRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        
        List<TrainerClientMapping> mappings = mappingRepository.findActiveClientsByTrainerId(trainerId);
        
        return mappings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all active trainers for a client
     */
    public List<TrainerClientMappingDTO> getClientTrainers(Long clientId) {
        userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        
        List<TrainerClientMapping> mappings = mappingRepository.findActiveTrainersByClientId(clientId);
        
        return mappings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Unassign client from trainer
     */
    public void unassignClient(Long trainerId, Long clientId) {
        TrainerClientMapping mapping = mappingRepository.findActiveMapping(trainerId, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));
        
        mapping.setIsActive(false);
        mappingRepository.save(mapping);
        log.info("Client {} unassigned from trainer {}", clientId, trainerId);
    }
    
    /**
     * Convert TrainerClientMapping entity to DTO
     */
    private TrainerClientMappingDTO convertToDTO(TrainerClientMapping mapping) {
        return TrainerClientMappingDTO.builder()
                .id(mapping.getId())
                .trainerId(mapping.getTrainer().getId())
                .trainerName(mapping.getTrainer().getName())
                .clientId(mapping.getClient().getId())
                .clientName(mapping.getClient().getName())
                .isActive(mapping.getIsActive())
                .createdAt(mapping.getCreatedAt())
                .build();
    }
}
