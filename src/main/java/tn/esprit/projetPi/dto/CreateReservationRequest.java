package tn.esprit.projetPi.dto;

import lombok.*;
import tn.esprit.projetPi.entities.ReservationType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateReservationRequest {
    private ReservationType type;
    
    // Campsite reservation
    private String campsiteId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    
    // Event reservation
    private String eventId;
    private String ticketTier;
    private Integer numberOfTickets;
    
    // Contact info
    private String userName;
    private String userEmail;
    private String userPhone;
    
    // Special requests
    private String specialRequests;
    private List<String> addOns;
    
    // Payment
    private String paymentMethod;
}
