package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "reservations")
public class Reservation {

    @Id
    String id;

    ReservationType type;
    
    // User info
    String userId;
    String userName;
    String userEmail;
    String userPhone;
    
    // Campsite reservation
    String campsiteId;
    String campsiteName;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    Integer numberOfGuests;
    Integer numberOfNights;
    
    // Event reservation
    String eventId;
    String eventTitle;
    String ticketTier;
    Integer numberOfTickets;
    
    // Pricing
    Double basePrice;
    Double cleaningFee;
    Double serviceFee;
    Double taxes;
    Double totalPrice;
    String currency;
    
    // Payment
    String paymentMethod;
    String paymentStatus;
    String transactionId;
    LocalDateTime paidAt;
    
    // Status
    ReservationStatus status;
    String cancellationReason;
    LocalDateTime cancelledAt;
    Double refundAmount;
    
    // Special requests
    String specialRequests;
    List<String> addOns;
    
    // Confirmation
    String confirmationCode;
    Boolean isConfirmed;
    LocalDateTime confirmedAt;
    
    // Check-in/Check-out
    Boolean checkedIn;
    LocalDateTime checkedInAt;
    Boolean checkedOut;
    LocalDateTime checkedOutAt;
    
    // Timestamps
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
