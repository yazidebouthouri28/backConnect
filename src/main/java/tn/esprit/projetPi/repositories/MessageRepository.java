package tn.esprit.projetPi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Message;
import tn.esprit.projetPi.entities.MessageType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    
    List<Message> findByChatRoomIdOrderByCreatedAtDesc(String chatRoomId);
    
    Page<Message> findByChatRoomIdOrderByCreatedAtDesc(String chatRoomId, Pageable pageable);
    
    List<Message> findBySenderIdOrderByCreatedAtDesc(String senderId);
    
    List<Message> findByRecipientIdAndIsReadFalse(String recipientId);
    
    @Query("{'$or': [{'senderId': ?0, 'recipientId': ?1}, {'senderId': ?1, 'recipientId': ?0}], 'messageType': 'DIRECT'}")
    List<Message> findDirectMessagesBetweenUsers(String userId1, String userId2);
    
    List<Message> findByMessageType(MessageType type);
    
    @Query(value = "{'chatRoomId': ?0, 'isDeleted': {$ne: true}}", sort = "{'createdAt': -1}")
    List<Message> findActiveChatRoomMessages(String chatRoomId);
    
    @Query(value = "{'recipientId': ?0, 'isRead': false}", count = true)
    Long countUnreadMessages(String userId);
    
    @Query("{'chatRoomId': ?0, 'createdAt': {$gt: ?1}}")
    List<Message> findNewMessagesSince(String chatRoomId, LocalDateTime since);
}
