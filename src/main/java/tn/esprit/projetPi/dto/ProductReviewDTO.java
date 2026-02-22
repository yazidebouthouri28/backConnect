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
public class ProductReviewDTO {
    private String id;
    private String productId;
    private String productName;
    private String userId;
    private String userName;
    private String userAvatar;
    private String orderId;
    
    private Integer rating;
    private String title;
    private String comment;
    private List<String> images;
    
    private Boolean verified;
    private Boolean approved;
    private Boolean featured;
    
    private Integer helpfulCount;
    
    private String sellerResponse;
    private LocalDateTime sellerResponseAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
