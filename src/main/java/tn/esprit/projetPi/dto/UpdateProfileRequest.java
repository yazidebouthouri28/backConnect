package tn.esprit.projetPi.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileRequest {
    private String name;
    private String phone;
    private String address;
    private String country;
    private Long age;
    
    // Profile fields
    private String avatar;
    private String bio;
    private String location;
    private String website;
    private List<String> interests;
    private Map<String, String> socialLinks;
}
