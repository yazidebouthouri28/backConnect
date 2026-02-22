package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.Role;
import tn.esprit.projetPi.entities.ShippingAddress;
import tn.esprit.projetPi.entities.User;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
        user.setUpdatedAt(LocalDateTime.now());
        
        return toDTO(userRepository.save(user));
    }

    public UserProfileDTO updateAvatar(String userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setAvatar(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        return toDTO(userRepository.save(user));
    }

    public UserProfileDTO addShippingAddress(String userId, ShippingAddressDTO addressDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        ShippingAddress address = new ShippingAddress();
        address.setId(UUID.randomUUID().toString());
        address.setFullName(addressDTO.getFullName());
        address.setPhone(addressDTO.getPhone());
        address.setEmail(addressDTO.getEmail());
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setCountry(addressDTO.getCountry());
        address.setLabel(addressDTO.getLabel());
        
        if (user.getShippingAddresses() == null) {
            user.setShippingAddresses(new ArrayList<>());
        }

        // If this is the first address or marked as default, set it as default
        if (user.getShippingAddresses().isEmpty() || Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            address.setIsDefault(true);
            // Unset other defaults
            user.getShippingAddresses().forEach(a -> a.setIsDefault(false));
            user.setDefaultShippingAddressId(address.getId());
        } else {
            address.setIsDefault(false);
        }

        user.getShippingAddresses().add(address);
        user.setUpdatedAt(LocalDateTime.now());
        
        return toDTO(userRepository.save(user));
    }

    public UserProfileDTO updateShippingAddress(String userId, String addressId, ShippingAddressDTO addressDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getShippingAddresses() == null) {
            throw new ResourceNotFoundException("Address not found");
        }

        ShippingAddress address = user.getShippingAddresses().stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (addressDTO.getFullName() != null) address.setFullName(addressDTO.getFullName());
        if (addressDTO.getPhone() != null) address.setPhone(addressDTO.getPhone());
        if (addressDTO.getEmail() != null) address.setEmail(addressDTO.getEmail());
        if (addressDTO.getAddressLine1() != null) address.setAddressLine1(addressDTO.getAddressLine1());
        if (addressDTO.getAddressLine2() != null) address.setAddressLine2(addressDTO.getAddressLine2());
        if (addressDTO.getCity() != null) address.setCity(addressDTO.getCity());
        if (addressDTO.getState() != null) address.setState(addressDTO.getState());
        if (addressDTO.getPostalCode() != null) address.setPostalCode(addressDTO.getPostalCode());
        if (addressDTO.getCountry() != null) address.setCountry(addressDTO.getCountry());
        if (addressDTO.getLabel() != null) address.setLabel(addressDTO.getLabel());

        if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            user.getShippingAddresses().forEach(a -> a.setIsDefault(false));
            address.setIsDefault(true);
            user.setDefaultShippingAddressId(address.getId());
        }

        user.setUpdatedAt(LocalDateTime.now());
        return toDTO(userRepository.save(user));
    }

    public UserProfileDTO removeShippingAddress(String userId, String addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getShippingAddresses() != null) {
            user.getShippingAddresses().removeIf(a -> a.getId().equals(addressId));
            if (addressId.equals(user.getDefaultShippingAddressId())) {
                user.setDefaultShippingAddressId(
                        user.getShippingAddresses().isEmpty() ? null : user.getShippingAddresses().get(0).getId()
                );
                if (!user.getShippingAddresses().isEmpty()) {
                    user.getShippingAddresses().get(0).setIsDefault(true);
                }
            }
        }

        user.setUpdatedAt(LocalDateTime.now());
        return toDTO(userRepository.save(user));
    }

    public UserProfileDTO becomeSeller(String userId, String storeName, String storeDescription) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setIsSeller(true);
        user.setStoreName(storeName);
        user.setStoreDescription(storeDescription);
        user.setSellerSince(LocalDateTime.now());
        user.setSellerVerified(false);
        user.setTotalSales(0);
        user.setSellerRating(null);
        user.setSellerReviewCount(0);
        if (user.getRole() == Role.USER || user.getRole() == Role.BUYER) {
            user.setRole(Role.SELLER);
        }
        user.setUpdatedAt(LocalDateTime.now());

        log.info("User {} became a seller: {}", userId, storeName);
        return toDTO(userRepository.save(user));
    }

    public void updateLastLogin(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public PageResponse<UserProfileDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage = userRepository.findAll(pageable);
        return toPageResponse(userPage);
    }

    public PageResponse<UserProfileDTO> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // Fixed: Now properly searches by username, name, AND email
        Page<User> userPage = userRepository.searchUsers(query, pageable);
        return toPageResponse(userPage);
    }

    public PageResponse<UserProfileDTO> getUsersByRole(Role role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage = userRepository.findByRole(role, pageable);
        return toPageResponse(userPage);
    }

    public PageResponse<UserProfileDTO> getSellers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sellerSince").descending());
        Page<User> userPage = userRepository.findByIsSeller(true, pageable);
        return toPageResponse(userPage);
    }

    public UserProfileDTO updateUserStatus(String userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (request.getIsActive() != null) user.setIsActive(request.getIsActive());
        if (request.getIsSuspended() != null) {
            user.setIsSuspended(request.getIsSuspended());
            if (request.getIsSuspended()) {
                user.setSuspendedAt(LocalDateTime.now());
                user.setSuspensionReason(request.getSuspensionReason());
                user.setSuspendedUntil(request.getSuspendedUntil());
            } else {
                user.setSuspendedAt(null);
                user.setSuspensionReason(null);
                user.setSuspendedUntil(null);
            }
        }
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getSellerVerified() != null) user.setSellerVerified(request.getSellerVerified());
        user.setUpdatedAt(LocalDateTime.now());

        log.info("User {} status updated", userId);
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

    private PageResponse<UserProfileDTO> toPageResponse(Page<User> page) {
        return PageResponse.<UserProfileDTO>builder()
                .content(page.getContent().stream().map(this::toDTO).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
