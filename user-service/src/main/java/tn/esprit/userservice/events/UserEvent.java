package tn.esprit.userservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    public enum EventType {
        CREATED, UPDATED, DELETED
    }

    private EventType eventType;
    private String userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private Map<String, Object> changes;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
