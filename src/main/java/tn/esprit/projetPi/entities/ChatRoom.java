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
@Document(collection = "chatrooms")
public class ChatRoom {

    @Id
    String id;

    String name;
    String description;
    String image;
    ChatRoomType type;
    
    // Related entity (for campsite/event specific chats)
    String relatedEntityId;
    String relatedEntityType;
    
    // Members
    List<String> memberIds;
    List<String> adminIds;
    String creatorId;
    
    // Settings
    Boolean isPublic;
    Boolean allowJoin;
    Integer maxMembers;
    
    // Last message info
    String lastMessageId;
    String lastMessageContent;
    String lastMessageSenderId;
    LocalDateTime lastMessageAt;
    
    // Timestamps
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
