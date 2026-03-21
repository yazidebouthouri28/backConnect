package tn.esprit.projetintegre.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetintegre.enums.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private Long organizerId;
    private String username;
    private String email;
    private String name;
    private Role role;

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private AuthResponse authResponse = new AuthResponse();

        public AuthResponseBuilder token(String token) {
            authResponse.token = token;
            return this;
        }

        public AuthResponseBuilder tokenType(String tokenType) {
            authResponse.tokenType = tokenType;
            return this;
        }

        public AuthResponseBuilder userId(Long userId) {
            authResponse.userId = userId;
            return this;
        }

        public AuthResponseBuilder organizerId(Long organizerId) {
            authResponse.organizerId = organizerId;
            return this;
        }

        public AuthResponseBuilder username(String username) {
            authResponse.username = username;
            return this;
        }

        public AuthResponseBuilder email(String email) {
            authResponse.email = email;
            return this;
        }

        public AuthResponseBuilder name(String name) {
            authResponse.name = name;
            return this;
        }

        public AuthResponseBuilder role(Role role) {
            authResponse.role = role;
            return this;
        }

        public AuthResponse build() {
            return authResponse;
        }
    }
}
