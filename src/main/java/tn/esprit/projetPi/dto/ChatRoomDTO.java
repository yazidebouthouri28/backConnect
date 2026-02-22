package tn.esprit.projetPi.dto;

import lombok.*;
import tn.esprit.projetPi.entities.ChatRoomType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDTO {
    private String id;
    private String name;
    private String description;
    private String image;
    private ChatRoomType type;
    
    // Related entity
    private String relatedEntityId;
    private String relatedEntityType;
    
    // Members
    private List<String> memberIds;
    private List<String> adminIds;
    private String creatorId;
    private Integer memberCount;
    
    // Settings
    private Boolean isPublic;
    private Boolean allowJoin;
    private Integer maxMembers;
    
    // Last message info
    private String lastMessageId;
    private String lastMessageContent;
    private String lastMessageSenderId;
    private LocalDateTime lastMessageAt;
    
    // Unread count for current user
    private Integer unreadCount;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
