package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "product_reviews")
public class ProductReview {

    @Id
    String id;

    String productId;
    String productName;
    String userId;
    String userName;
    String userAvatar;
    String orderId; // The order where the product was purchased
    
    Integer rating; // 1-5
    String title;
    String comment;
    
    List<String> images; // Review images
    
    Boolean verified; // Verified purchase
    Boolean approved; // Admin approved
    Boolean featured;
    
    Integer helpfulCount;
    List<String> helpfulVotes; // User IDs who found this helpful
    
    String sellerResponse;
    LocalDateTime sellerResponseAt;
    
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
