package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "users")
public class User {

    @Id
    String id;  // MongoDB ObjectId

    // Informations personnelles
    String name;
    String username;
    String email;
    String password;
    String phone;
    String address;
    String country;
    Long age;

    Integer loyaltyPoints;
    LocalDateTime createdAt;

    // Role pour gestion des droits
    Role role;

    // Profile fields
    String avatar;
    String bio;
    String location;
    String website;
    List<String> interests;
    Map<String, String> socialLinks;
    Boolean emailVerified;
    Boolean phoneVerified;
    LocalDateTime lastLoginAt;
    Boolean isActive;
    
    // Camping preferences
    List<String> favoriteCampsites;
    List<String> favoriteEvents;
    Integer totalCampingTrips;
    Integer totalEventsAttended;
    Double averageRating;
    Integer reviewCount;

    // Relations
    @DBRef
    List<ForumArticle> articles;  // Articles créés par l'utilisateur

    @DBRef
    List<ChatMessage> messages;   // Messages envoyés par l'utilisateur
}
