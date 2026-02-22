package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.CreateReservationRequest;
import tn.esprit.projetPi.dto.ReservationDTO;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.CampsiteRepository;
import tn.esprit.projetPi.repositories.EventRepository;
import tn.esprit.projetPi.repositories.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CampsiteRepository campsiteRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO getReservationById(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        return toDTO(reservation);
    }

    public ReservationDTO getReservationByConfirmationCode(String code) {
        Reservation reservation = reservationRepository.findByConfirmationCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with confirmation code: " + code));
        return toDTO(reservation);
    }

    public List<ReservationDTO> getReservationsByUser(String userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> getCampsiteReservations(String campsiteId) {
        return reservationRepository.findByCampsiteId(campsiteId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> getEventReservations(String eventId) {
        return reservationRepository.findByEventId(eventId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO createReservation(CreateReservationRequest request, String userId) {
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setUserName(request.getUserName());
        reservation.setUserEmail(request.getUserEmail());
        reservation.setUserPhone(request.getUserPhone());
        reservation.setType(request.getType());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setAddOns(request.getAddOns());
        reservation.setPaymentMethod(request.getPaymentMethod());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setConfirmationCode(generateConfirmationCode());
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        reservation.setCurrency("USD");
        
        if (request.getType() == ReservationType.CAMPSITE) {
            // Campsite reservation
            Campsite campsite = campsiteRepository.findById(request.getCampsiteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campsite not found"));
            
            // Check availability
            if (!checkCampsiteAvailability(request.getCampsiteId(), request.getCheckInDate(), request.getCheckOutDate())) {
                throw new IllegalStateException("Campsite is not available for the selected dates");
            }
            
            reservation.setCampsiteId(request.getCampsiteId());
            reservation.setCampsiteName(campsite.getName());
            reservation.setCheckInDate(request.getCheckInDate());
            reservation.setCheckOutDate(request.getCheckOutDate());
            reservation.setNumberOfGuests(request.getNumberOfGuests());
            
            long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
            reservation.setNumberOfNights((int) nights);
            
            // Calculate pricing
            double basePrice = campsite.getPricePerNight() * nights;
            double cleaningFee = campsite.getCleaningFee() != null ? campsite.getCleaningFee() : 0;
            double serviceFee = campsite.getServiceFee() != null ? campsite.getServiceFee() : 0;
            double taxes = basePrice * 0.1; // 10% tax
            
            reservation.setBasePrice(basePrice);
            reservation.setCleaningFee(cleaningFee);
            reservation.setServiceFee(serviceFee);
            reservation.setTaxes(taxes);
            reservation.setTotalPrice(basePrice + cleaningFee + serviceFee + taxes);
            
        } else if (request.getType() == ReservationType.EVENT) {
            // Event reservation
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
            
            // Check availability
            if (!eventService.hasAvailableSpots(request.getEventId())) {
                throw new IllegalStateException("Event is sold out");
            }
            
            // Check if user already registered
            if (reservationRepository.findUserEventRegistration(userId, request.getEventId()).isPresent()) {
                throw new IllegalStateException("User already registered for this event");
            }
            
            reservation.setEventId(request.getEventId());
            reservation.setEventTitle(event.getTitle());
            reservation.setTicketTier(request.getTicketTier());
            reservation.setNumberOfTickets(request.getNumberOfTickets() != null ? request.getNumberOfTickets() : 1);
            
            // Calculate pricing
            double ticketPrice = event.getPrice() != null ? event.getPrice() : 0;
            double basePrice = ticketPrice * reservation.getNumberOfTickets();
            double serviceFee = basePrice * 0.05; // 5% service fee
            double taxes = basePrice * 0.1; // 10% tax
            
            reservation.setBasePrice(basePrice);
            reservation.setServiceFee(serviceFee);
            reservation.setTaxes(taxes);
            reservation.setTotalPrice(basePrice + serviceFee + taxes);
            
            // Increment event registration count
            eventService.incrementRegistrationCount(request.getEventId());
        }
        
        return toDTO(reservationRepository.save(reservation));
    }

    public ReservationDTO confirmReservation(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setIsConfirmed(true);
        reservation.setConfirmedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        
        return toDTO(reservationRepository.save(reservation));
    }

    public ReservationDTO cancelReservation(String id, String reason) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancellationReason(reason);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        
        // Decrement event registration count if applicable
        if (reservation.getType() == ReservationType.EVENT && reservation.getEventId() != null) {
            eventService.decrementRegistrationCount(reservation.getEventId());
        }
        
        return toDTO(reservationRepository.save(reservation));
    }

    public ReservationDTO checkIn(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        
        reservation.setCheckedIn(true);
        reservation.setCheckedInAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        
        return toDTO(reservationRepository.save(reservation));
    }

    public ReservationDTO checkOut(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        
        reservation.setCheckedOut(true);
        reservation.setCheckedOutAt(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setUpdatedAt(LocalDateTime.now());
        
        return toDTO(reservationRepository.save(reservation));
    }

    public boolean checkCampsiteAvailability(String campsiteId, LocalDate checkIn, LocalDate checkOut) {
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(campsiteId, checkIn, checkOut);
        return conflicts.isEmpty();
    }

    public List<ReservationDTO> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO processPayment(String id, String transactionId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        
        reservation.setTransactionId(transactionId);
        reservation.setPaymentStatus("PAID");
        reservation.setPaidAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        
        return toDTO(reservationRepository.save(reservation));
    }

    private String generateConfirmationCode() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ReservationDTO toDTO(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .type(reservation.getType())
                .userId(reservation.getUserId())
                .userName(reservation.getUserName())
                .userEmail(reservation.getUserEmail())
                .userPhone(reservation.getUserPhone())
                .campsiteId(reservation.getCampsiteId())
                .campsiteName(reservation.getCampsiteName())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .numberOfGuests(reservation.getNumberOfGuests())
                .numberOfNights(reservation.getNumberOfNights())
                .eventId(reservation.getEventId())
                .eventTitle(reservation.getEventTitle())
                .ticketTier(reservation.getTicketTier())
                .numberOfTickets(reservation.getNumberOfTickets())
                .basePrice(reservation.getBasePrice())
                .cleaningFee(reservation.getCleaningFee())
                .serviceFee(reservation.getServiceFee())
                .taxes(reservation.getTaxes())
                .totalPrice(reservation.getTotalPrice())
                .currency(reservation.getCurrency())
                .paymentMethod(reservation.getPaymentMethod())
                .paymentStatus(reservation.getPaymentStatus())
                .transactionId(reservation.getTransactionId())
                .paidAt(reservation.getPaidAt())
                .status(reservation.getStatus())
                .cancellationReason(reservation.getCancellationReason())
                .cancelledAt(reservation.getCancelledAt())
                .refundAmount(reservation.getRefundAmount())
                .specialRequests(reservation.getSpecialRequests())
                .addOns(reservation.getAddOns())
                .confirmationCode(reservation.getConfirmationCode())
                .isConfirmed(reservation.getIsConfirmed())
                .confirmedAt(reservation.getConfirmedAt())
                .checkedIn(reservation.getCheckedIn())
                .checkedInAt(reservation.getCheckedInAt())
                .checkedOut(reservation.getCheckedOut())
                .checkedOutAt(reservation.getCheckedOutAt())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }
}
