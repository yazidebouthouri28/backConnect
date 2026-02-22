package tn.esprit.projetPi.dto;

import lombok.*;
import tn.esprit.projetPi.entities.ReviewTargetType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDTO {
    private String id;
    
    // Reviewer info
    private String userId;
    private String userName;
    private String userAvatar;
    
    // Review target
    private ReviewTargetType targetType;
    private String targetId;
    private String targetName;
    
    // Related reservation
    private String reservationId;
    
    // Rating
    private Integer rating;
    private Double overallScore;
    
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
    
    // Response from owner/organizer
    private String ownerResponse;
    private LocalDateTime ownerResponseAt;
    private String ownerId;
    
    // Status
    private Boolean isVerified;
    private Boolean isApproved;
    private Boolean isHidden;
    private Integer helpfulCount;
    
    // Current user info
    private Boolean isHelpfulByCurrentUser;
    
    // Report
    private Boolean isReported;
    private String reportReason;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
