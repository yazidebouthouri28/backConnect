package tn.esprit.projetintegre.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetintegre.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String country;
    private Integer age;
    private Role role;
    private Boolean isSeller;
    private Boolean isBuyer;
    private String storeName;
    private BigDecimal sellerRating;
    private String avatar;
    private String bio;
    private Boolean isActive;
    private Integer loyaltyPoints;
    private String loyaltyTier;
    private Integer experiencePoints;
    private Integer level;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public Integer getAge() {
        return age;
    }

    public Role getRole() {
        return role;
    }

    public Boolean getIsSeller() {
        return isSeller;
    }

    public Boolean getIsBuyer() {
        return isBuyer;
    }

    public String getStoreName() {
        return storeName;
    }

    public BigDecimal getSellerRating() {
        return sellerRating;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getBio() {
        return bio;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    public Integer getExperiencePoints() {
        return experiencePoints;
    }

    public Integer getLevel() {
        return level;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }

    public static class UserDTOBuilder {
        private UserDTO userDTO = new UserDTO();

        public UserDTOBuilder id(Long id) {
            userDTO.id = id;
            return this;
        }

        public UserDTOBuilder name(String name) {
            userDTO.name = name;
            return this;
        }

        public UserDTOBuilder username(String username) {
            userDTO.username = username;
            return this;
        }

        public UserDTOBuilder email(String email) {
            userDTO.email = email;
            return this;
        }

        public UserDTOBuilder phone(String phone) {
            userDTO.phone = phone;
            return this;
        }

        public UserDTOBuilder address(String address) {
            userDTO.address = address;
            return this;
        }

        public UserDTOBuilder country(String country) {
            userDTO.country = country;
            return this;
        }

        public UserDTOBuilder age(Integer age) {
            userDTO.age = age;
            return this;
        }

        public UserDTOBuilder role(Role role) {
            userDTO.role = role;
            return this;
        }

        public UserDTOBuilder isSeller(Boolean isSeller) {
            userDTO.isSeller = isSeller;
            return this;
        }

        public UserDTOBuilder isBuyer(Boolean isBuyer) {
            userDTO.isBuyer = isBuyer;
            return this;
        }

        public UserDTOBuilder storeName(String storeName) {
            userDTO.storeName = storeName;
            return this;
        }

        public UserDTOBuilder sellerRating(BigDecimal sellerRating) {
            userDTO.sellerRating = sellerRating;
            return this;
        }

        public UserDTOBuilder avatar(String avatar) {
            userDTO.avatar = avatar;
            return this;
        }

        public UserDTOBuilder bio(String bio) {
            userDTO.bio = bio;
            return this;
        }

        public UserDTOBuilder isActive(Boolean isActive) {
            userDTO.isActive = isActive;
            return this;
        }

        public UserDTOBuilder loyaltyPoints(Integer loyaltyPoints) {
            userDTO.loyaltyPoints = loyaltyPoints;
            return this;
        }

        public UserDTOBuilder loyaltyTier(String loyaltyTier) {
            userDTO.loyaltyTier = loyaltyTier;
            return this;
        }

        public UserDTOBuilder experiencePoints(Integer experiencePoints) {
            userDTO.experiencePoints = experiencePoints;
            return this;
        }

        public UserDTOBuilder level(Integer level) {
            userDTO.level = level;
            return this;
        }

        public UserDTOBuilder createdAt(LocalDateTime createdAt) {
            userDTO.createdAt = createdAt;
            return this;
        }

        public UserDTOBuilder lastLogin(LocalDateTime lastLogin) {
            userDTO.lastLogin = lastLogin;
            return this;
        }

        public UserDTO build() {
            return userDTO;
        }
    }
}
