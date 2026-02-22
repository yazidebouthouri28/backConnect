package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageReaction {
    String userId;
    String userName;
    String emoji;
    LocalDateTime createdAt;
}
