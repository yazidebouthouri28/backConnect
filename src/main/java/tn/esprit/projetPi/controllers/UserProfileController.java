package tn.esprit.projetPi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.ApiResponse;
import tn.esprit.projetPi.dto.PageResponse;
import tn.esprit.projetPi.dto.UpdateProfileRequest;
import tn.esprit.projetPi.dto.UserProfileDTO;
import tn.esprit.projetPi.services.UserProfileService;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDTO profile = userProfileService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", profile));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getProfileById(@PathVariable String userId) {
        UserProfileDTO profile = userProfileService.getProfile(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", profile));
    }

    @GetMapping("/profile/username/{username}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getProfileByUsername(@PathVariable String username) {
        UserProfileDTO profile = userProfileService.getProfileByUsername(username);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateMyProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDTO profile = userProfileService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", profile));
    }

    @PatchMapping("/profile/avatar")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateAvatar(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDTO profile = userProfileService.updateAvatar(userDetails.getUsername(), body.get("avatarUrl"));
        return ResponseEntity.ok(new ApiResponse<>(true, "Avatar updated successfully", profile));
    }

    @PostMapping("/favorites/campsites/{campsiteId}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> addFavoriteCampsite(
            @PathVariable String campsiteId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDTO profile = userProfileService.addFavoriteCampsite(userDetails.getUsername(), campsiteId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Campsite added to favorites", profile));
    }

    @DeleteMapping("/favorites/campsites/{campsiteId}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> removeFavoriteCampsite(
            @PathVariable String campsiteId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDTO profile = userProfileService.removeFavoriteCampsite(userDetails.getUsername(), campsiteId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Campsite removed from favorites", profile));
    }

    @PostMapping("/favorites/events/{eventId}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> addFavoriteEvent(
            @PathVariable String eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDTO profile = userProfileService.addFavoriteEvent(userDetails.getUsername(), eventId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event added to favorites", profile));
    }

    @DeleteMapping("/favorites/events/{eventId}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> removeFavoriteEvent(
            @PathVariable String eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDTO profile = userProfileService.removeFavoriteEvent(userDetails.getUsername(), eventId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event removed from favorites", profile));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserProfileDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<UserProfileDTO> users = userProfileService.getAllUsers(page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<UserProfileDTO>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<UserProfileDTO> users = userProfileService.searchUsers(query, page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Search completed successfully", users));
    }
}
