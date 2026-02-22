package tn.esprit.projetPi.dto;

import lombok.*;
import tn.esprit.projetPi.entities.ReservationStatus;
import tn.esprit.projetPi.entities.ReservationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDTO {
    private String id;
    private ReservationType type;
    
    // User info
    private String userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    
    // Campsite reservation
    private String campsiteId;
    private String campsiteName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private Integer numberOfNights;
    
    // Event reservation
    private String eventId;
    private String eventTitle;
    private String ticketTier;
    private Integer numberOfTickets;
    
    // Pricing
    private Double basePrice;
    private Double cleaningFee;
    private Double serviceFee;
    private Double taxes;
    private Double totalPrice;
    private String currency;
    
    // Payment
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
    private LocalDateTime paidAt;
    
    // Status
    private ReservationStatus status;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private Double refundAmount;
    
    // Special requests
    private String specialRequests;
    private List<String> addOns;
    
    // Confirmation
    private String confirmationCode;
    private Boolean isConfirmed;
    private LocalDateTime confirmedAt;
    
    // Check-in/Check-out
    private Boolean checkedIn;
    private LocalDateTime checkedInAt;
    private Boolean checkedOut;
    private LocalDateTime checkedOutAt;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
