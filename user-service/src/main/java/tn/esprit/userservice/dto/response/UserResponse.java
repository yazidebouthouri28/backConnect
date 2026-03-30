package tn.esprit.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String address;
    private String role;
    private Boolean enabled;
    private Boolean emailVerified;
    private String avatar;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
