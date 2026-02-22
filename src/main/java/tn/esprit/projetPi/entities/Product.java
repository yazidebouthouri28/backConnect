package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "products")
public class Product {

    @Id
    String id;

    String name;
    String description;
    String shortDescription;

    BigDecimal price;
    BigDecimal rentalPrice;
    BigDecimal compareAtPrice;

    String sku;

    List<String> tags;
    List<String> images;

    Boolean isActive;
    Boolean isFeatured;
    Boolean inStock;

    Integer stockQuantity;
    Integer lowStockThreshold;
    Boolean rentalAvailable;

    Integer loyaltyPoints;
    Double rating;
    Integer reviews;

    String seoTitle;
    String seoDescription;

    Integer discount;
    List<String> specs;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
