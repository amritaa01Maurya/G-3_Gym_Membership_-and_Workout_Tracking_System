package com.g_3.gym_ms.controller;

import com.g_3.gym_ms.dto.LoginRequest;
import com.g_3.gym_ms.dto.LoginResponse;
import com.g_3.gym_ms.dto.SignupRequest;
import com.g_3.gym_ms.dto.UserDTO;
import com.g_3.gym_ms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody SignupRequest request) {
        log.info("POST /api/auth/signup - Signup request for email: {}", request.email());
        UserDTO user = authService.signup(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - Login request for email: {}", request.email());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
