package org.example.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gateway.dto.AuthRequest;
import org.example.gateway.dto.AuthResponse;
import org.example.gateway.dto.RegisterRequest;
import org.example.gateway.response.AuthenticationResponse;
import org.example.gateway.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
} 