package tn.esprit.projetPi.dto;

import lombok.*;
import tn.esprit.projetPi.entities.MessageType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendMessageRequest {
    private String recipientId;
    private String chatRoomId;
    private MessageType messageType;
    private String content;
    private List<String> attachments;
    private List<String> images;
    private String replyToId;
}
