package org.example.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gateway.dto.AuthRequest;
import org.example.gateway.dto.AuthResponse;
import org.example.gateway.dto.RegisterRequest;
import org.example.gateway.dto.OwnerDto;
import org.example.gateway.dto.AuthenticationResponse;
import org.example.gateway.model.User;
import org.example.gateway.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserService userService;
    private final OwnerService ownerService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Регистрация нового юзера: {}", request.getUsername());
        log.info("Полученный пароль: {}", request.getPassword());

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.info("Захешированный пароль: {}", encodedPassword);

        if (!passwordEncoder.matches(request.getPassword(), encodedPassword)) {
            throw new RuntimeException("Пароль не совпадает с кодировкой");
        }

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setName(request.getUsername());
        
        OwnerDto createdOwner = ownerService.createOwner(ownerDto);
        log.info("Создан овнер: {}", createdOwner);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setRole(User.Role.USER);
        user.setOwnerId(createdOwner.getId());

        User savedUser = userService.createUser(user);
        log.info("Создан пользователь: {}", savedUser);

        if (!passwordEncoder.matches(request.getPassword(), savedUser.getPassword())) {
            throw new RuntimeException("Пароль не совпадает с сохраненным захешированным");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse authenticate(RegisterRequest request) {
        log.info("Авторизация пользователя: {}", request.getUsername());
        log.info("Пароль из запроса: {}", request.getPassword());

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        User user = userService.getUserByUsername(request.getUsername());
        log.info("Найден пользователь: {}", user);
        log.info("Его захешированный пароль: {}", user.getPassword());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Пароли не совпадают: {}", request.getUsername());
            throw new RuntimeException("PПароль не совпадает: " + request.getUsername());
        }

        String token = jwtService.generateToken(user);
        log.info("jwt token: {}", request.getUsername());

        return new AuthResponse(token);
    }
}