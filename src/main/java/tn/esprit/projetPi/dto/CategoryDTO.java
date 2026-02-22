package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private String id;
    private String name;
    private String description;
    private String slug;
    private String image;
    private String icon;
    
    private String parentId;
    private Integer level;
    private String path;
    
    private List<CategoryDTO> children;
    
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer sortOrder;
    
    private Integer productCount;
    
    private String seoTitle;
    private String seoDescription;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
