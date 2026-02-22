package tn.esprit.projetPi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.Role;
import tn.esprit.projetPi.services.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AdminController {

    private final AdminService adminService;
    private final UserProfileService userProfileService;


    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getDashboard() {
        AdminDashboardDTO dashboard = adminService.getDashboardStats();
        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard stats retrieved successfully", dashboard));
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserProfileDTO>>> getAllUsers() {
        List<UserProfileDTO> users = userProfileService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable String userId,
            @RequestBody Map<String, String> body) {
        Role role = Role.valueOf(body.get("role"));
        adminService.updateUserRole(userId, role);
        return ResponseEntity.ok(new ApiResponse<>(true, "User role updated successfully", null));
    }

    @PatchMapping("/users/{userId}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleUserActive(@PathVariable String userId) {
        adminService.toggleUserActive(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User status toggled successfully", null));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }


}
