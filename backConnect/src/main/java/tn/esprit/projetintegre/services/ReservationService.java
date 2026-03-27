package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.projetintegre.entities.Event;
import tn.esprit.projetintegre.entities.Reservation;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.entities.User;
import tn.esprit.projetintegre.enums.PaymentStatus;
import tn.esprit.projetintegre.enums.ReservationStatus;
import tn.esprit.projetintegre.exception.ResourceNotFoundException;
import tn.esprit.projetintegre.repositories.EventRepository;
import tn.esprit.projetintegre.repositories.ReservationRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;
import tn.esprit.projetintegre.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SiteRepository siteRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final tn.esprit.projetintegre.repositories.CampsiteRepository campsiteRepository;
    private final tn.esprit.projetintegre.repositories.PackRepository packRepository;
    private final tn.esprit.projetintegre.repositories.CampingServiceRepository campingServiceRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Page<Reservation> getReservationsByUser(Long userId, Pageable pageable) {
        return reservationRepository.findByUserId(userId, pageable);
    }

    public Page<Reservation> getReservationsBySite(Long siteId, Pageable pageable) {
        return reservationRepository.findBySiteId(siteId, pageable);
    }

    public Page<Reservation> getReservationsByStatus(ReservationStatus status, Pageable pageable) {
        return reservationRepository.findByStatus(status, pageable);
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    public Reservation getReservationByNumber(String reservationNumber) {
        return reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }

    @Transactional
    public Reservation createSiteReservation(Long userId, Long siteId, Long campsiteId, Long packId, List<Long> serviceIds,
                                              LocalDateTime checkIn, LocalDateTime checkOut, int numberOfGuests,
                                              String guestName, String guestEmail, String guestPhone,
                                              String specialRequests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found"));

        tn.esprit.projetintegre.entities.Campsite campsite = null;
        if (campsiteId != null) {
            campsite = campsiteRepository.findById(campsiteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Campsite not found"));
        }

        tn.esprit.projetintegre.entities.Pack pack = null;
        if (packId != null) {
            pack = packRepository.findById(packId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pack not found"));
        }

        List<tn.esprit.projetintegre.entities.CampingService> services = new ArrayList<>();
        if (serviceIds != null && !serviceIds.isEmpty()) {
            services = campingServiceRepository.findAllById(serviceIds);
        }

        // Check availability
        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(siteId, checkIn, checkOut);
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Site is not available for the selected dates");
        }

        long nights = ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
        if (nights == 0) nights = 1; // Minimum 1 night charge

        // Base price calculation (Campsite price or Site price)
        BigDecimal basePrice = campsite != null ? campsite.getPricePerNight() : site.getPricePerNight();
        BigDecimal stayPrice = basePrice.multiply(BigDecimal.valueOf(nights));

        // Add Pack price
        BigDecimal packPrice = pack != null ? pack.getPrice() : BigDecimal.ZERO;

        // Add Services prices
        BigDecimal servicesPrice = services.stream()
                .map(tn.esprit.projetintegre.entities.CampingService::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPrice = stayPrice.add(packPrice).add(servicesPrice);

        Reservation reservation = Reservation.builder()
                .user(user)
                .site(site)
                .campsite(campsite)
                .pack(pack)
                .services(services)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .numberOfGuests(numberOfGuests)
                .numberOfNights((int) nights)
                .pricePerNight(basePrice)
                .totalPrice(totalPrice)
                .status(ReservationStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .guestName(guestName)
                .guestEmail(guestEmail)
                .guestPhone(guestPhone)
                .specialRequests(specialRequests)
                .build();

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation createEventReservation(Long userId, Long eventId, String guestName,
                                               String guestEmail, String guestPhone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (event.getMaxParticipants() != null && 
            event.getCurrentParticipants() >= event.getMaxParticipants()) {
            throw new IllegalStateException("Event is fully booked");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .event(event)
                .checkInDate(event.getStartDate())
                .checkOutDate(event.getEndDate())
                .numberOfGuests(1)
                .totalPrice(event.getIsFree() ? BigDecimal.ZERO : event.getPrice())
                .status(ReservationStatus.PENDING)
                .paymentStatus(event.getIsFree() ? PaymentStatus.COMPLETED : PaymentStatus.PENDING)
                .guestName(guestName)
                .guestEmail(guestEmail)
                .guestPhone(guestPhone)
                .build();

        reservation = reservationRepository.save(reservation);

        // Update event participant count
        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        eventRepository.save(event);

        return reservation;
    }

    @Transactional
    public Reservation confirmReservation(Long id) {
        Reservation reservation = getReservationById(id);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setConfirmedAt(LocalDateTime.now());
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation cancelReservation(Long id, String reason) {
        Reservation reservation = getReservationById(id);
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancellationReason(reason);
        reservation.setCancelledAt(LocalDateTime.now());

        // If event reservation, decrease participant count
        if (reservation.getEvent() != null) {
            Event event = reservation.getEvent();
            event.setCurrentParticipants(event.getCurrentParticipants() - 1);
            eventRepository.save(event);
        }

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation updatePaymentStatus(Long id, PaymentStatus status, String transactionId) {
        Reservation reservation = getReservationById(id);
        reservation.setPaymentStatus(status);
        reservation.setPaymentTransactionId(transactionId);
        if (status == PaymentStatus.COMPLETED) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setConfirmedAt(LocalDateTime.now());
        }
        return reservationRepository.save(reservation);
    }
}
