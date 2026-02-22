package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "categories")
public class Category {

    @Id
    String id;

    String name;
    String description;
    String slug;
    String image;
    String icon;
    
    String parentId; // For nested categories
    Integer level; // 0 for root, 1 for first level, etc.
    String path; // Full path like "/electronics/phones/smartphones"
    
    List<Category> children = new ArrayList<>();
    
    Boolean isActive;
    Boolean isFeatured;
    Integer sortOrder;
    
    Integer productCount;
    
    // SEO
    String seoTitle;
    String seoDescription;
    String metaKeywords;
    
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
