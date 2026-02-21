package com.camping.projet.controller;

import com.camping.projet.dto.request.UserRequest;
import com.camping.projet.dto.response.UserResponse;
import com.camping.projet.enums.Role;
import com.camping.projet.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/camping/{campingId}")
    public ResponseEntity<List<UserResponse>> getUsersByCamping(@PathVariable Long campingId) {
        return ResponseEntity.ok(userService.getUsersByCamping(campingId));
    }

    @PatchMapping("/{id}/photo")
    public ResponseEntity<Void> updatePhoto(@PathVariable Long id, @RequestParam String photoUrl) {
        userService.updateProfilePhoto(id, photoUrl);
        return ResponseEntity.ok().build();
    }
}
