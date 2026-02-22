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
@Document(collection = "messages")
public class Message {

    @Id
    String id;

    // Sender info
    String senderId;
    String senderName;
    String senderAvatar;
    
    // Recipient (for direct messages)
    String recipientId;
    String recipientName;
    
    // Chat room (for group chats)
    String chatRoomId;
    String chatRoomName;
    MessageType messageType;
    
    // Content
    String content;
    List<String> attachments;
    List<String> images;
    
    // Reply
    String replyToId;
    String replyToContent;
    
    // Status
    Boolean isRead;
    LocalDateTime readAt;
    Boolean isDeleted;
    Boolean isEdited;
    LocalDateTime editedAt;
    
    // Reactions
    List<MessageReaction> reactions;
    
    // Timestamps
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
