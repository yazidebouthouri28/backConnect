package tn.esprit.projetintegre.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.projetintegre.entities.ChatMessage;
import tn.esprit.projetintegre.enums.MessageType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);

    Page<ChatMessage> findBySenderId(Long senderId, Pageable pageable);

    List<ChatMessage> findByChatRoomIdAndCreatedAtAfter(Long chatRoomId, LocalDateTime after);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.type = :type ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findByRoomIdAndType(@Param("roomId") Long roomId, @Param("type") MessageType type, Pageable pageable);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.isDeleted = false ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findActiveMessagesByRoomId(@Param("roomId") Long roomId, Pageable pageable);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.isRead = false AND cm.sender.id != :userId")
    List<ChatMessage> findUnreadMessagesByRoomIdAndNotUser(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.isRead = false AND cm.sender.id != :userId")
    Long countUnreadMessages(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true, cm.readAt = :readAt WHERE cm.chatRoom.id = :roomId AND cm.sender.id != :userId AND cm.isRead = false")
    int markMessagesAsRead(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND LOWER(cm.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY cm.createdAt DESC")
    Page<ChatMessage> searchInRoom(@Param("roomId") Long roomId, @Param("keyword") String keyword, Pageable pageable);
}
