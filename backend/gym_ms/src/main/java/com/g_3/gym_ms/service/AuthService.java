package com.g_3.gym_ms.service;

import com.g_3.gym_ms.dto.LoginRequest;
import com.g_3.gym_ms.dto.LoginResponse;
import com.g_3.gym_ms.dto.SignupRequest;
import com.g_3.gym_ms.dto.UserDTO;
import com.g_3.gym_ms.entity.Role;
import com.g_3.gym_ms.entity.RoleEnum;
import com.g_3.gym_ms.entity.User;
import com.g_3.gym_ms.exception.BadRequestException;
import com.g_3.gym_ms.exception.ResourceNotFoundException;
import com.g_3.gym_ms.repository.RoleRepository;
import com.g_3.gym_ms.repository.UserRepository;
import com.g_3.gym_ms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    public UserDTO signup(SignupRequest request) {
        log.info("Attempting signup for email: {}", request.email());
        
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Signup failed: Email already exists: {}", request.email());
            throw new BadRequestException("Email already registered");
        }
        
        Role role = roleRepository.findByName(RoleEnum.valueOf(request.role().toUpperCase()))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.role()));
        
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getId());
        
        return convertToDTO(savedUser);
    }
    
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for email: {}", request.email());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            String token = jwtTokenProvider.generateTokenFromEmail(
                    user.getEmail(),
                    user.getId(),
                    user.getRole().getName().name()
            );
            
            log.info("User login successful: {}", user.getId());
            
            return LoginResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole().getName().name())
                    .build();
        } catch (Exception ex) {
            log.error("Login failed for email: {}", request.email(), ex);
            throw new BadRequestException("Invalid email or password");
        }
    }
    
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().getName().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
