package tn.esprit.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.userservice.dto.request.LoginRequest;
import tn.esprit.userservice.dto.request.RegisterRequest;
import tn.esprit.userservice.dto.response.LoginResponse;
import tn.esprit.userservice.dto.response.UserResponse;
import tn.esprit.userservice.entity.User;
import tn.esprit.userservice.enums.Role;
import tn.esprit.userservice.events.UserEventPublisher;
import tn.esprit.userservice.exception.DuplicateResourceException;
import tn.esprit.userservice.exception.ResourceNotFoundException;
import tn.esprit.userservice.repository.UserRepository;
import tn.esprit.userservice.security.JwtTokenProvider;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserEventPublisher userEventPublisher;
    private final UserService userService;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole() != null ? request.getRole().toUpperCase() : "CLIENT");
        } catch (IllegalArgumentException e) {
            role = Role.CLIENT;
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(role)                  // ← dynamic now
                .enabled(true)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);

        // Publish user created event
        try {
            userEventPublisher.publishUserCreated(user);
        } catch (Exception e) {
            log.warn("Failed to publish user.created event: {}", e.getMessage());
        }

        String token = jwtTokenProvider.generateToken(user);

        log.info("User registered successfully: {}", user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsernameOrEmail(
                        request.getUsername(), request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "username", request.getUsername()));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);

        log.info("User logged in: {}", user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    public UserResponse getCurrentUser(String username) {
        return userService.getUserByUsername(username);
    }
}
