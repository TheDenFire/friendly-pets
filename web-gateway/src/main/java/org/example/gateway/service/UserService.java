package org.example.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gateway.model.User;
import org.example.gateway.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByUsername(String username) {
        log.info("Looking up user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public User getUserByOwnerId(Long ownerId) {
        return userRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(User user) {
        log.info("Creating new user with username: {}", user.getUsername());
        log.info("Password before saving: {}", user.getPassword());
        // Password is already encoded in AuthenticationService
        User savedUser = userRepository.save(user);
        log.info("Password after saving: {}", savedUser.getPassword());
        return savedUser;
    }

    public User updateUser(Long id, User user) {
        log.info("Updating user with id: {}", id);
        User existingUser = getUserById(id);
        
        // Update fields while preserving the existing password hash
        existingUser.setUsername(user.getUsername());
        existingUser.setOwnerId(user.getOwnerId());
        existingUser.setRole(user.getRole());
        
        log.info("Preserving existing password hash: {}", existingUser.getPassword());
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user details for username: {}", username);
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        log.info("Found user: {}", user);
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    public User findByUsername(String username) {
        log.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElse(null);
    }
} 