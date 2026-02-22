package tn.esprit.projetPi.dto;

import lombok.*;
import tn.esprit.projetPi.entities.ReviewTargetType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateReviewRequest {
    private ReviewTargetType targetType;
    private String targetId;
    private String reservationId;
    
    // Rating
    private Integer rating;
    
    // Detailed ratings
    private Integer cleanlinessRating;
    private Integer locationRating;
    private Integer valueRating;
    private Integer amenitiesRating;
    private Integer serviceRating;
    private Integer accuracyRating;
    
    // Content
    private String title;
    private String content;
    private List<String> images;
    
    // Pros and Cons
    private List<String> pros;
    private List<String> cons;
}
