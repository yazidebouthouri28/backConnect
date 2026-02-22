package tn.esprit.projetPi.dto;

import lombok.*;
import tn.esprit.projetPi.entities.MessageReaction;
import tn.esprit.projetPi.entities.MessageType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private String id;
    
    // Sender info
    private String senderId;
    private String senderName;
    private String senderAvatar;
    
    // Recipient (for direct messages)
    private String recipientId;
    private String recipientName;
    
    // Chat room (for group chats)
    private String chatRoomId;
    private String chatRoomName;
    private MessageType messageType;
    
    // Content
    private String content;
    private List<String> attachments;
    private List<String> images;
    
    // Reply
    private String replyToId;
    private String replyToContent;
    
    // Status
    private Boolean isRead;
    private LocalDateTime readAt;
    private Boolean isDeleted;
    private Boolean isEdited;
    private LocalDateTime editedAt;
    
    // Reactions
    private List<MessageReaction> reactions;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
