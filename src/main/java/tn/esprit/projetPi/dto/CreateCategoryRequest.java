package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
    
    private String description;
    private String image;
    private String icon;
    private String parentId;
    private Integer sortOrder;
    private String seoTitle;
    private String seoDescription;
    private String metaKeywords;
}
