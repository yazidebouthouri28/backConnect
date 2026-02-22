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
@Document(collection = "reviews")
public class Review {

    @Id
    String id;

    // Reviewer info
    String userId;
    String userName;
    String userAvatar;
    
    // Review target
    ReviewTargetType targetType;
    String targetId;
    String targetName;
    
    // Related reservation (if applicable)
    String reservationId;
    
    // Rating
    Integer rating; // 1-5
    Double overallScore;
    
    // Detailed ratings
    Integer cleanlinessRating;
    Integer locationRating;
    Integer valueRating;
    Integer amenitiesRating;
    Integer serviceRating;
    Integer accuracyRating;
    
    // Content
    String title;
    String content;
    List<String> images;
    
    // Pros and Cons
    List<String> pros;
    List<String> cons;
    
    // Response from owner/organizer
    String ownerResponse;
    LocalDateTime ownerResponseAt;
    String ownerId;
    
    // Status
    Boolean isVerified;
    Boolean isApproved;
    Boolean isHidden;
    Integer helpfulCount;
    List<String> helpfulByUserIds;
    
    // Report
    Boolean isReported;
    String reportReason;
    
    // Timestamps
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
