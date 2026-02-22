package tn.esprit.projetPi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.ApiResponse;
import tn.esprit.projetPi.dto.CreateReservationRequest;
import tn.esprit.projetPi.dto.ReservationDTO;
import tn.esprit.projetPi.entities.ReservationStatus;
import tn.esprit.projetPi.services.ReservationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservations retrieved successfully", reservations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationDTO>> getReservationById(@PathVariable String id) {
        ReservationDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservation retrieved successfully", reservation));
    }

    @GetMapping("/confirmation/{code}")
    public ResponseEntity<ApiResponse<ReservationDTO>> getReservationByConfirmationCode(@PathVariable String code) {
        ReservationDTO reservation = reservationService.getReservationByConfirmationCode(code);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservation retrieved successfully", reservation));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getMyReservations(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ReservationDTO> reservations = reservationService.getReservationsByUser(userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, "Your reservations retrieved successfully", reservations));
    }

    @GetMapping("/campsite/{campsiteId}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getCampsiteReservations(@PathVariable String campsiteId) {
        List<ReservationDTO> reservations = reservationService.getCampsiteReservations(campsiteId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Campsite reservations retrieved successfully", reservations));
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getEventReservations(@PathVariable String eventId) {
        List<ReservationDTO> reservations = reservationService.getEventReservations(eventId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event reservations retrieved successfully", reservations));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getReservationsByStatus(@PathVariable ReservationStatus status) {
        List<ReservationDTO> reservations = reservationService.getReservationsByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservations retrieved successfully", reservations));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationDTO>> createReservation(
            @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReservationDTO created = reservationService.createReservation(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Reservation created successfully", created));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReservationDTO>> confirmReservation(@PathVariable String id) {
        ReservationDTO reservation = reservationService.confirmReservation(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservation confirmed successfully", reservation));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationDTO>> cancelReservation(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        ReservationDTO reservation = reservationService.cancelReservation(id, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservation cancelled successfully", reservation));
    }

    @PatchMapping("/{id}/check-in")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReservationDTO>> checkIn(@PathVariable String id) {
        ReservationDTO reservation = reservationService.checkIn(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Check-in successful", reservation));
    }

    @PatchMapping("/{id}/check-out")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReservationDTO>> checkOut(@PathVariable String id) {
        ReservationDTO reservation = reservationService.checkOut(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Check-out successful", reservation));
    }

    @PatchMapping("/{id}/payment")
    public ResponseEntity<ApiResponse<ReservationDTO>> processPayment(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String transactionId = body.get("transactionId");
        ReservationDTO reservation = reservationService.processPayment(id, transactionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment processed successfully", reservation));
    }

    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @RequestParam String campsiteId,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut) {
        boolean available = reservationService.checkCampsiteAvailability(campsiteId, checkIn, checkOut);
        return ResponseEntity.ok(new ApiResponse<>(true, "Availability checked", available));
    }
}
