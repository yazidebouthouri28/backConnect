package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseDTO {
    private String id;
    
    @NotBlank(message = "Warehouse name is required")
    private String name;
    
    @NotBlank(message = "Warehouse code is required")
    private String code;
    
    private String address;
    private String city;
    private String country;
    private String phone;
    private String email;
    private Boolean isActive = true;
}
