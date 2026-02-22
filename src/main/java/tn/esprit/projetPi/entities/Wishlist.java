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
@Document(collection = "wishlists")
public class Wishlist {

    @Id
    String id;

    String userId;
    String name; // Allow multiple wishlists
    Boolean isDefault;
    Boolean isPublic;
    
    List<WishlistItem> items = new ArrayList<>();
    
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
