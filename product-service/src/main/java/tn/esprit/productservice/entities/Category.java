package tn.esprit.productservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotBlank(message = "Category name is required")
    @Column(unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    private String image;
    private String slug;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Integer displayOrder = 0;

    // LAZY is fine for parent — it's a single object, and we guard it
    // in the mapper with Hibernate.isInitialized()
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"subcategories", "products", "parent", "hibernateLazyInitializer", "handler"})
    private Category parent;

    // FIX: EAGER — Jackson serializes this list; it must be loaded in-session
    // @JsonIgnoreProperties breaks the infinite parent→subcategory→parent loop
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    @JsonIgnoreProperties({"parent", "products", "subcategories", "hibernateLazyInitializer", "handler"})
    private List<Category> subcategories = new ArrayList<>();

    // FIX: EAGER — needed when CategoryResponse.productCount calls .size()
    // @JsonIgnoreProperties breaks the Category→Product→Category loop
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    @JsonIgnoreProperties({"category", "reviews", "images", "tags", "hibernateLazyInitializer", "handler"})
    private List<Product> products = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (slug == null || slug.isEmpty()) {
            slug = name.toLowerCase().replaceAll("\\s+", "-");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}