package tn.esprit.userservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tn.esprit.userservice.entity.User;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserCreated(User user) {
        UserEvent event = UserEvent.builder()
                .eventType(UserEvent.EventType.CREATED)
                .userId(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("user.created", user.getId().toString(), event);
        kafkaTemplate.send("user.registered", user.getId().toString(), event);
        log.info("Published user.created event for user: {}", user.getUsername());
    }

    public void publishUserUpdated(User user, Map<String, Object> changes) {
        UserEvent event = UserEvent.builder()
                .eventType(UserEvent.EventType.UPDATED)
                .userId(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .changes(changes)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("user.updated", user.getId().toString(), event);
        log.info("Published user.updated event for user: {}", user.getUsername());
    }
}
