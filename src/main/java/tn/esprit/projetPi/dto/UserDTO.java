package tn.esprit.projetPi.dto;

import lombok.Data;
import tn.esprit.projetPi.entities.Role;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private String id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String country;
    private Long age;
    private Integer loyaltyPoints;
    private Role role;
    private LocalDateTime createdAt;
}
