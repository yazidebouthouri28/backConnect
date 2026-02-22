package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "email_notifications")
public class EmailNotification {

    @Id
    String id;

    String userId;
    String toEmail;
    String toName;
    
    EmailType type;
    String subject;
    String body;
    String htmlBody;
    
    Map<String, String> templateData;
    
    Boolean sent;
    LocalDateTime sentAt;
    String errorMessage;
    Integer retryCount;
    
    LocalDateTime createdAt;
}
