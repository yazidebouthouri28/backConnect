package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.ChatRoom;
import tn.esprit.projetPi.entities.ChatRoomType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    
    @Query("{'memberIds': ?0}")
    List<ChatRoom> findByMemberId(String memberId);
    
    List<ChatRoom> findByType(ChatRoomType type);
    
    List<ChatRoom> findByIsPublicTrue();
    
    Optional<ChatRoom> findByRelatedEntityIdAndRelatedEntityType(String entityId, String entityType);
    
    @Query("{'memberIds': {$all: [?0, ?1]}, 'type': 'DIRECT'}")
    Optional<ChatRoom> findDirectChatBetweenUsers(String userId1, String userId2);
    
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<ChatRoom> searchByName(String name);
    
    List<ChatRoom> findByCreatorId(String creatorId);
}
