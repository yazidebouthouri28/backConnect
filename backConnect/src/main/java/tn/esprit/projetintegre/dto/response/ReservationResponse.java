package tn.esprit.projetintegre.dto.response;

import lombok.*;
import tn.esprit.projetintegre.enums.ReservationStatus;
import tn.esprit.projetintegre.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private String reservationNumber;
    private Long userId;
    private String userName;
    private Long siteId;
    private String siteName;
    private Long campsiteId;
    private String campsiteName;
    private Long packId;
    private String packName;
    private java.util.List<String> serviceNames;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfNights;
    private Integer numberOfGuests;
    private BigDecimal pricePerNight;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private PaymentStatus paymentStatus;
    private String specialRequests;
    private String contactPhone;
    private String contactEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
