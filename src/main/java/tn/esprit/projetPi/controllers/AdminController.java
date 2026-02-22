package tn.esprit.projetPi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.Role;
import tn.esprit.projetPi.services.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AdminController {

    private final AdminService adminService;
    private final UserProfileService userProfileService;
    private final CampsiteService campsiteService;
    private final EventService eventService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;
    private final SponsorService sponsorService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getDashboard() {
        AdminDashboardDTO dashboard = adminService.getDashboardStats();
        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard stats retrieved successfully", dashboard));
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserProfileDTO>>> getAllUsers() {
        List<UserProfileDTO> users = userProfileService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable String userId,
            @RequestBody Map<String, String> body) {
        Role role = Role.valueOf(body.get("role"));
        adminService.updateUserRole(userId, role);
        return ResponseEntity.ok(new ApiResponse<>(true, "User role updated successfully", null));
    }

    @PatchMapping("/users/{userId}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleUserActive(@PathVariable String userId) {
        adminService.toggleUserActive(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User status toggled successfully", null));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }

    // Campsite Management
    @GetMapping("/campsites")
    public ResponseEntity<ApiResponse<List<CampsiteDTO>>> getAllCampsites() {
        List<CampsiteDTO> campsites = campsiteService.getAllCampsites();
        return ResponseEntity.ok(new ApiResponse<>(true, "Campsites retrieved successfully", campsites));
    }

    @PatchMapping("/campsites/{id}/toggle-featured")
    public ResponseEntity<ApiResponse<CampsiteDTO>> toggleCampsiteFeatured(@PathVariable String id) {
        CampsiteDTO campsite = campsiteService.toggleFeatured(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Campsite featured status toggled", campsite));
    }

    @PatchMapping("/campsites/{id}/toggle-active")
    public ResponseEntity<ApiResponse<CampsiteDTO>> toggleCampsiteActive(@PathVariable String id) {
        CampsiteDTO campsite = campsiteService.toggleActive(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Campsite active status toggled", campsite));
    }

    @DeleteMapping("/campsites/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCampsite(@PathVariable String id) {
        campsiteService.deleteCampsite(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Campsite deleted successfully", null));
    }

    // Event Management
    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(new ApiResponse<>(true, "Events retrieved successfully", events));
    }

    @PatchMapping("/events/{id}/toggle-featured")
    public ResponseEntity<ApiResponse<EventDTO>> toggleEventFeatured(@PathVariable String id) {
        EventDTO event = eventService.toggleFeatured(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event featured status toggled", event));
    }

    @PatchMapping("/events/{id}/cancel")
    public ResponseEntity<ApiResponse<EventDTO>> cancelEvent(@PathVariable String id) {
        EventDTO event = eventService.cancelEvent(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event cancelled successfully", event));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event deleted successfully", null));
    }

    // Reservation Management
    @GetMapping("/reservations")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservations retrieved successfully", reservations));
    }

    @PatchMapping("/reservations/{id}/confirm")
    public ResponseEntity<ApiResponse<ReservationDTO>> confirmReservation(@PathVariable String id) {
        ReservationDTO reservation = reservationService.confirmReservation(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservation confirmed successfully", reservation));
    }

    @PatchMapping("/reservations/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationDTO>> cancelReservation(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : "Cancelled by admin";
        ReservationDTO reservation = reservationService.cancelReservation(id, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservation cancelled successfully", reservation));
    }

    // Review Management
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(new ApiResponse<>(true, "Reviews retrieved successfully", reviews));
    }

    @GetMapping("/reviews/pending")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getPendingReviews() {
        List<ReviewDTO> reviews = reviewService.getPendingReviews();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending reviews retrieved successfully", reviews));
    }

    @GetMapping("/reviews/reported")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReportedReviews() {
        List<ReviewDTO> reviews = reviewService.getReportedReviews();
        return ResponseEntity.ok(new ApiResponse<>(true, "Reported reviews retrieved successfully", reviews));
    }

    @PatchMapping("/reviews/{id}/approve")
    public ResponseEntity<ApiResponse<ReviewDTO>> approveReview(@PathVariable String id) {
        ReviewDTO review = reviewService.approveReview(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review approved successfully", review));
    }

    @PatchMapping("/reviews/{id}/hide")
    public ResponseEntity<ApiResponse<ReviewDTO>> hideReview(@PathVariable String id) {
        ReviewDTO review = reviewService.hideReview(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review hidden successfully", review));
    }

    // Sponsor Management
    @GetMapping("/sponsors")
    public ResponseEntity<ApiResponse<List<SponsorDTO>>> getAllSponsors() {
        List<SponsorDTO> sponsors = sponsorService.getAllSponsors();
        return ResponseEntity.ok(new ApiResponse<>(true, "Sponsors retrieved successfully", sponsors));
    }

    @PostMapping("/sponsors")
    public ResponseEntity<ApiResponse<SponsorDTO>> createSponsor(@RequestBody SponsorDTO dto) {
        SponsorDTO sponsor = sponsorService.createSponsor(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Sponsor created successfully", sponsor));
    }

    @PutMapping("/sponsors/{id}")
    public ResponseEntity<ApiResponse<SponsorDTO>> updateSponsor(
            @PathVariable String id,
            @RequestBody SponsorDTO dto) {
        SponsorDTO sponsor = sponsorService.updateSponsor(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Sponsor updated successfully", sponsor));
    }

    @DeleteMapping("/sponsors/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSponsor(@PathVariable String id) {
        sponsorService.deleteSponsor(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Sponsor deleted successfully", null));
    }
}
