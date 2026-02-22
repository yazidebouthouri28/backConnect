package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {
    private String id;
    
    @NotBlank(message = "Category name is required")
    private String name;
    
    private String description;
    private String icon;
    private Integer productCount;
}
