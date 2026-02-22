package tn.esprit.projetPi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.services.RefundService;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RefundRequestDTO>>> getMyRefunds(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<RefundRequestDTO> refunds = refundService.getRefundRequestsByUser(
                userDetails.getUsername(), page, size);
        return ResponseEntity.ok(ApiResponse.success(refunds));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RefundRequestDTO>> getRefund(@PathVariable String id) {
        RefundRequestDTO refund = refundService.getRefundRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(refund));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RefundRequestDTO>> createRefundRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateRefundRequest request) {
        RefundRequestDTO refund = refundService.createRefundRequest(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Refund request submitted", refund));
    }

    @PutMapping("/{id}/tracking")
    public ResponseEntity<ApiResponse<RefundRequestDTO>> updateTrackingNumber(
            @PathVariable String id,
            @RequestParam String trackingNumber) {
        RefundRequestDTO refund = refundService.updateReturnTrackingNumber(id, trackingNumber);
        return ResponseEntity.ok(ApiResponse.success("Tracking number updated", refund));
    }
}
