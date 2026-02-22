package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.ChatRoomDTO;
import tn.esprit.projetPi.dto.MessageDTO;
import tn.esprit.projetPi.dto.SendMessageRequest;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.ChatRoomRepository;
import tn.esprit.projetPi.repositories.MessageRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    // Message operations
    public MessageDTO sendMessage(SendMessageRequest request, String senderId, String senderName, String senderAvatar) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setSenderAvatar(senderAvatar);
        message.setRecipientId(request.getRecipientId());
        message.setChatRoomId(request.getChatRoomId());
        message.setMessageType(request.getMessageType() != null ? request.getMessageType() : MessageType.DIRECT);
        message.setContent(request.getContent());
        message.setAttachments(request.getAttachments());
        message.setImages(request.getImages());
        message.setReplyToId(request.getReplyToId());
        message.setIsRead(false);
        message.setIsDeleted(false);
        message.setIsEdited(false);
        message.setReactions(new ArrayList<>());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        
        // Update chat room last message
        if (request.getChatRoomId() != null) {
            chatRoomRepository.findById(request.getChatRoomId()).ifPresent(room -> {
                room.setLastMessageId(message.getId());
                room.setLastMessageContent(request.getContent());
                room.setLastMessageSenderId(senderId);
                room.setLastMessageAt(LocalDateTime.now());
                chatRoomRepository.save(room);
            });
        }
        
        return toDTO(messageRepository.save(message));
    }

    public MessageDTO getMessageById(String id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        return toDTO(message);
    }

    public List<MessageDTO> getChatRoomMessages(String chatRoomId) {
        return messageRepository.findActiveChatRoomMessages(chatRoomId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Page<MessageDTO> getChatRoomMessages(String chatRoomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable)
                .map(this::toDTO);
    }

    public List<MessageDTO> getDirectMessages(String userId1, String userId2) {
        return messageRepository.findDirectMessagesBetweenUsers(userId1, userId2).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getUnreadMessages(String userId) {
        return messageRepository.findByRecipientIdAndIsReadFalse(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount(String userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    public void markAsRead(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        message.setIsRead(true);
        message.setReadAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    public void markAllAsRead(String chatRoomId, String userId) {
        List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
        messages.stream()
                .filter(m -> !userId.equals(m.getSenderId()) && !Boolean.TRUE.equals(m.getIsRead()))
                .forEach(m -> {
                    m.setIsRead(true);
                    m.setReadAt(LocalDateTime.now());
                    messageRepository.save(m);
                });
    }

    public MessageDTO editMessage(String messageId, String newContent, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        
        if (!userId.equals(message.getSenderId())) {
            throw new IllegalStateException("Cannot edit another user's message");
        }
        
        message.setContent(newContent);
        message.setIsEdited(true);
        message.setEditedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        
        return toDTO(messageRepository.save(message));
    }

    public void deleteMessage(String messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        
        if (!userId.equals(message.getSenderId())) {
            throw new IllegalStateException("Cannot delete another user's message");
        }
        
        message.setIsDeleted(true);
        message.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    public MessageDTO addReaction(String messageId, String userId, String userName, String emoji) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        
        if (message.getReactions() == null) {
            message.setReactions(new ArrayList<>());
        }
        
        // Remove existing reaction from this user
        message.getReactions().removeIf(r -> r.getUserId().equals(userId));
        
        // Add new reaction
        MessageReaction reaction = new MessageReaction();
        reaction.setUserId(userId);
        reaction.setUserName(userName);
        reaction.setEmoji(emoji);
        reaction.setCreatedAt(LocalDateTime.now());
        message.getReactions().add(reaction);
        
        return toDTO(messageRepository.save(message));
    }

    // Chat Room operations
    public ChatRoomDTO createChatRoom(String name, String description, ChatRoomType type, String creatorId, List<String> memberIds) {
        ChatRoom room = new ChatRoom();
        room.setName(name);
        room.setDescription(description);
        room.setType(type);
        room.setCreatorId(creatorId);
        room.setMemberIds(memberIds);
        room.setAdminIds(List.of(creatorId));
        room.setIsPublic(type == ChatRoomType.PUBLIC);
        room.setAllowJoin(true);
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());
        
        return toChatRoomDTO(chatRoomRepository.save(room));
    }

    public ChatRoomDTO getChatRoomById(String id) {
        ChatRoom room = chatRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + id));
        return toChatRoomDTO(room);
    }

    public List<ChatRoomDTO> getUserChatRooms(String userId) {
        return chatRoomRepository.findByMemberId(userId).stream()
                .map(this::toChatRoomDTO)
                .collect(Collectors.toList());
    }

    public List<ChatRoomDTO> getPublicChatRooms() {
        return chatRoomRepository.findByIsPublicTrue().stream()
                .map(this::toChatRoomDTO)
                .collect(Collectors.toList());
    }

    public ChatRoomDTO getOrCreateDirectChat(String userId1, String userId2) {
        return chatRoomRepository.findDirectChatBetweenUsers(userId1, userId2)
                .map(this::toChatRoomDTO)
                .orElseGet(() -> createChatRoom("Direct Chat", null, ChatRoomType.DIRECT, userId1, List.of(userId1, userId2)));
    }

    public ChatRoomDTO joinChatRoom(String roomId, String userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + roomId));
        
        if (!room.getMemberIds().contains(userId)) {
            room.getMemberIds().add(userId);
            room.setUpdatedAt(LocalDateTime.now());
            chatRoomRepository.save(room);
        }
        
        return toChatRoomDTO(room);
    }

    public void leaveChatRoom(String roomId, String userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + roomId));
        
        room.getMemberIds().remove(userId);
        room.setUpdatedAt(LocalDateTime.now());
        chatRoomRepository.save(room);
    }

    private MessageDTO toDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .senderAvatar(message.getSenderAvatar())
                .recipientId(message.getRecipientId())
                .recipientName(message.getRecipientName())
                .chatRoomId(message.getChatRoomId())
                .chatRoomName(message.getChatRoomName())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .attachments(message.getAttachments())
                .images(message.getImages())
                .replyToId(message.getReplyToId())
                .replyToContent(message.getReplyToContent())
                .isRead(message.getIsRead())
                .readAt(message.getReadAt())
                .isDeleted(message.getIsDeleted())
                .isEdited(message.getIsEdited())
                .editedAt(message.getEditedAt())
                .reactions(message.getReactions())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }

    private ChatRoomDTO toChatRoomDTO(ChatRoom room) {
        return ChatRoomDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .image(room.getImage())
                .type(room.getType())
                .relatedEntityId(room.getRelatedEntityId())
                .relatedEntityType(room.getRelatedEntityType())
                .memberIds(room.getMemberIds())
                .adminIds(room.getAdminIds())
                .creatorId(room.getCreatorId())
                .memberCount(room.getMemberIds() != null ? room.getMemberIds().size() : 0)
                .isPublic(room.getIsPublic())
                .allowJoin(room.getAllowJoin())
                .maxMembers(room.getMaxMembers())
                .lastMessageId(room.getLastMessageId())
                .lastMessageContent(room.getLastMessageContent())
                .lastMessageSenderId(room.getLastMessageSenderId())
                .lastMessageAt(room.getLastMessageAt())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
