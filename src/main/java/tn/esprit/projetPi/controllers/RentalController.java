package tn.esprit.projetPi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.ApiResponse;
import tn.esprit.projetPi.dto.CreateRentalRequest;
import tn.esprit.projetPi.dto.RentalDTO;
import tn.esprit.projetPi.services.RentalService;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RentalDTO>>> getAllRentals() {
        List<RentalDTO> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(ApiResponse.success(rentals));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RentalDTO>> getRentalById(@PathVariable String id) {
        RentalDTO rental = rentalService.getRentalById(id);
        return ResponseEntity.ok(ApiResponse.success(rental));
    }

    @GetMapping("/my-rentals")
    public ResponseEntity<ApiResponse<List<RentalDTO>>> getMyRentals(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        List<RentalDTO> rentals = rentalService.getRentalsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(rentals));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RentalDTO>>> getRentalsByUser(@PathVariable String userId) {
        List<RentalDTO> rentals = rentalService.getRentalsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(rentals));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RentalDTO>>> getActiveRentals() {
        List<RentalDTO> rentals = rentalService.getActiveRentals();
        return ResponseEntity.ok(ApiResponse.success(rentals));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<RentalDTO>>> getOverdueRentals() {
        List<RentalDTO> rentals = rentalService.getOverdueRentals();
        return ResponseEntity.ok(ApiResponse.success(rentals));
    }

    @GetMapping("/ending-soon")
    public ResponseEntity<ApiResponse<List<RentalDTO>>> getRentalsEndingSoon(
            @RequestParam(defaultValue = "7") int days) {
        List<RentalDTO> rentals = rentalService.getRentalsEndingSoon(days);
        return ResponseEntity.ok(ApiResponse.success(rentals));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RentalDTO>> createRental(
            Authentication authentication,
            @Valid @RequestBody CreateRentalRequest request) {
        String userId = (String) authentication.getPrincipal();
        RentalDTO created = rentalService.createRental(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rental created successfully", created));
    }

    @PostMapping("/{id}/extend")
    public ResponseEntity<ApiResponse<RentalDTO>> extendRental(
            @PathVariable String id,
            @RequestParam int additionalDays) {
        RentalDTO extended = rentalService.extendRental(id, additionalDays);
        return ResponseEntity.ok(ApiResponse.success("Rental extended successfully", extended));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse<RentalDTO>> returnRental(@PathVariable String id) {
        RentalDTO returned = rentalService.returnRental(id);
        return ResponseEntity.ok(ApiResponse.success("Rental returned successfully", returned));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<RentalDTO>> cancelRental(@PathVariable String id) {
        RentalDTO cancelled = rentalService.cancelRental(id);
        return ResponseEntity.ok(ApiResponse.success("Rental cancelled successfully", cancelled));
    }

    @PostMapping("/update-overdue")
    public ResponseEntity<ApiResponse<Void>> updateOverdueRentals() {
        rentalService.updateOverdueRentals();
        return ResponseEntity.ok(ApiResponse.success("Overdue rentals updated", null));
    }
}
