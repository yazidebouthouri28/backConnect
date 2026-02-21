package com.camping.projet.dto.response;

import com.camping.projet.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidatureServiceResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long eventServiceId;
    private String serviceName;
    private Long eventId;
    private UserResponse user;
    private EventServiceResponse eventService;
    private String message;
    private String skills;
    private ApplicationStatus status;
    private LocalDateTime applicationDate;
}
