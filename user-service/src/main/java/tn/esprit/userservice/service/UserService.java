package tn.esprit.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.userservice.dto.request.UpdateProfileRequest;
import tn.esprit.userservice.dto.response.UserResponse;
import tn.esprit.userservice.entity.User;
import tn.esprit.userservice.events.UserEventPublisher;
import tn.esprit.userservice.exception.ResourceNotFoundException;
import tn.esprit.userservice.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher userEventPublisher;

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToResponse(user);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Map<String, Object> changes = new HashMap<>();

        if (request.getFirstName() != null) {
            changes.put("firstName", request.getFirstName());
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            changes.put("lastName", request.getLastName());
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            changes.put("phone", request.getPhone());
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            changes.put("address", request.getAddress());
            user.setAddress(request.getAddress());
        }
        if (request.getAvatar() != null) {
            changes.put("avatar", request.getAvatar());
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            changes.put("bio", request.getBio());
            user.setBio(request.getBio());
        }

        user = userRepository.save(user);

        // Publish update event
        try {
            userEventPublisher.publishUserUpdated(user, changes);
        } catch (Exception e) {
            log.warn("Failed to publish user.updated event: {}", e.getMessage());
        }

        log.info("Updated profile for user: {}", username);
        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole().name())
                .enabled(user.getEnabled())
                .emailVerified(user.getEmailVerified())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
