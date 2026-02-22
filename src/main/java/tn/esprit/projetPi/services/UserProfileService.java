package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.UpdateProfileRequest;
import tn.esprit.projetPi.dto.UserProfileDTO;
import tn.esprit.projetPi.entities.User;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    public UserProfileDTO getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return toDTO(user);
    }

    public UserProfileDTO getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return toDTO(user);
    }

    public UserProfileDTO updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        if (request.getAge() != null) user.setAge(request.getAge());
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getLocation() != null) user.setLocation(request.getLocation());
        if (request.getWebsite() != null) user.setWebsite(request.getWebsite());
        if (request.getInterests() != null) user.setInterests(request.getInterests());
        if (request.getSocialLinks() != null) user.setSocialLinks(request.getSocialLinks());
        
        return toDTO(userRepository.save(user));
    }

    public UserProfileDTO updateAvatar(String userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setAvatar(avatarUrl);
        return toDTO(userRepository.save(user));
    }

    public UserProfileDTO addFavoriteCampsite(String userId, String campsiteId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (user.getFavoriteCampsites() == null) {
            user.setFavoriteCampsites(new ArrayList<>());
        }
        
        if (!user.getFavoriteCampsites().contains(campsiteId)) {
            user.getFavoriteCampsites().add(campsiteId);
            userRepository.save(user);
        }
        
        return toDTO(user);
    }

    public UserProfileDTO removeFavoriteCampsite(String userId, String campsiteId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (user.getFavoriteCampsites() != null) {
            user.getFavoriteCampsites().remove(campsiteId);
            userRepository.save(user);
        }
        
        return toDTO(user);
    }

    public UserProfileDTO addFavoriteEvent(String userId, String eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (user.getFavoriteEvents() == null) {
            user.setFavoriteEvents(new ArrayList<>());
        }
        
        if (!user.getFavoriteEvents().contains(eventId)) {
            user.getFavoriteEvents().add(eventId);
            userRepository.save(user);
        }
        
        return toDTO(user);
    }

    public UserProfileDTO removeFavoriteEvent(String userId, String eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (user.getFavoriteEvents() != null) {
            user.getFavoriteEvents().remove(eventId);
            userRepository.save(user);
        }
        
        return toDTO(user);
    }

    public void updateLastLogin(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void incrementCampingTrips(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setTotalCampingTrips((user.getTotalCampingTrips() != null ? user.getTotalCampingTrips() : 0) + 1);
        userRepository.save(user);
    }

    public void incrementEventsAttended(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setTotalEventsAttended((user.getTotalEventsAttended() != null ? user.getTotalEventsAttended() : 0) + 1);
        userRepository.save(user);
    }

    public List<UserProfileDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserProfileDTO> searchUsers(String query) {
        List<User> byUsername = userRepository.findByUsernameContainingIgnoreCase(query);
        List<User> byName = userRepository.findByNameContainingIgnoreCase(query);
        
        return byUsername.stream()
                .distinct()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private UserProfileDTO toDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .country(user.getCountry())
                .age(user.getAge())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .location(user.getLocation())
                .website(user.getWebsite())
                .interests(user.getInterests())
                .socialLinks(user.getSocialLinks())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .isActive(user.getIsActive())
                .loyaltyPoints(user.getLoyaltyPoints())
                .favoriteCampsites(user.getFavoriteCampsites())
                .favoriteEvents(user.getFavoriteEvents())
                .totalCampingTrips(user.getTotalCampingTrips())
                .totalEventsAttended(user.getTotalEventsAttended())
                .averageRating(user.getAverageRating())
                .reviewCount(user.getReviewCount())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
