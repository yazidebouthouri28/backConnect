package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.OrderRepository;
import tn.esprit.projetPi.repositories.RefundRequestRepository;
import tn.esprit.projetPi.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundService {

    private final RefundRequestRepository refundRequestRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final WalletService walletService;

    public PageResponse<RefundRequestDTO> getAllRefundRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RefundRequest> refundPage = refundRequestRepository.findAll(pageable);
        return toPageResponse(refundPage);
    }

    public PageResponse<RefundRequestDTO> getRefundRequestsByStatus(RefundStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RefundRequest> refundPage = refundRequestRepository.findByStatus(status, pageable);
        return toPageResponse(refundPage);
    }

    public PageResponse<RefundRequestDTO> getRefundRequestsByUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RefundRequest> refundPage = refundRequestRepository.findByUserId(userId, pageable);
        return toPageResponse(refundPage);
    }

    public PageResponse<RefundRequestDTO> getRefundRequestsBySeller(String sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RefundRequest> refundPage = refundRequestRepository.findBySellerId(sellerId, pageable);
        return toPageResponse(refundPage);
    }

    public RefundRequestDTO getRefundRequestById(String id) {
        RefundRequest refund = refundRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund request not found with id: " + id));
        return toDTO(refund);
    }

    public RefundRequestDTO createRefundRequest(String userId, CreateRefundRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.getOrderId()));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only request refunds for your own orders");
        }

        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Refund can only be requested for shipped or delivered orders");
        }

        // Check if refund already exists for this order
        if (refundRequestRepository.findByOrderIdAndUserId(request.getOrderId(), userId).isPresent()) {
            throw new IllegalStateException("A refund request already exists for this order");
        }

        RefundRequest refund = new RefundRequest();
        refund.setOrderId(request.getOrderId());
        refund.setUserId(userId);
        refund.setSellerId(order.getSellerId());
        refund.setType(request.getType());
        refund.setStatus(RefundStatus.PENDING);
        refund.setProductIds(request.getProductIds());
        refund.setQuantities(request.getQuantities());
        refund.setReason(request.getReason());
        refund.setDetailedDescription(request.getDetailedDescription());
        refund.setImages(request.getImages());
        refund.setRequestedAmount(request.getRequestedAmount() != null ? 
                request.getRequestedAmount() : order.getTotalAmount());
        refund.setCreatedAt(LocalDateTime.now());

        RefundRequest saved = refundRequestRepository.save(refund);
        log.info("Refund request created: {} for order {}", saved.getId(), request.getOrderId());
        return toDTO(saved);
    }

    public RefundRequestDTO approveRefundRequest(String id, String processedBy, BigDecimal approvedAmount, String notes) {
        RefundRequest refund = refundRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund request not found: " + id));

        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new IllegalStateException("Only pending refund requests can be approved");
        }

        refund.setStatus(RefundStatus.APPROVED);
        refund.setApprovedAmount(approvedAmount);
        refund.setAdminNotes(notes);
        refund.setProcessedBy(processedBy);
        refund.setProcessedAt(LocalDateTime.now());
        refund.setUpdatedAt(LocalDateTime.now());

        RefundRequest saved = refundRequestRepository.save(refund);

        // Send email notification
        User user = userRepository.findById(refund.getUserId()).orElse(null);
        if (user != null) {
            emailService.sendRefundApproved(saved, user.getEmail(), user.getName());
        }

        log.info("Refund request {} approved for amount {}", id, approvedAmount);
        return toDTO(saved);
    }

    public RefundRequestDTO rejectRefundRequest(String id, String processedBy, String rejectionReason) {
        RefundRequest refund = refundRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund request not found: " + id));

        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new IllegalStateException("Only pending refund requests can be rejected");
        }

        refund.setStatus(RefundStatus.REJECTED);
        refund.setRejectionReason(rejectionReason);
        refund.setProcessedBy(processedBy);
        refund.setProcessedAt(LocalDateTime.now());
        refund.setUpdatedAt(LocalDateTime.now());

        log.info("Refund request {} rejected: {}", id, rejectionReason);
        return toDTO(refundRequestRepository.save(refund));
    }

    public RefundRequestDTO updateRefundStatus(String id, RefundStatus status, String notes) {
        RefundRequest refund = refundRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund request not found: " + id));

        refund.setStatus(status);
        refund.setUpdatedAt(LocalDateTime.now());
        if (notes != null) {
            refund.setAdminNotes(notes);
        }

        // Handle completion
        if (status == RefundStatus.COMPLETED) {
            // Process wallet refund
            walletService.processRefund(refund.getUserId(), refund.getApprovedAmount(), refund.getOrderId());

            // Update order status
            orderRepository.findById(refund.getOrderId()).ifPresent(order -> {
                order.setStatus(OrderStatus.REFUNDED);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
            });

            // Send email
            User user = userRepository.findById(refund.getUserId()).orElse(null);
            if (user != null) {
                emailService.sendRefundCompleted(refund, user.getEmail(), user.getName());
            }
        }

        return toDTO(refundRequestRepository.save(refund));
    }

    public RefundRequestDTO updateReturnTrackingNumber(String id, String trackingNumber) {
        RefundRequest refund = refundRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund request not found: " + id));

        refund.setTrackingNumber(trackingNumber);
        refund.setStatus(RefundStatus.ITEMS_SHIPPED);
        refund.setUpdatedAt(LocalDateTime.now());

        return toDTO(refundRequestRepository.save(refund));
    }

    public RefundRequestDTO markItemsReceived(String id, String receivedBy) {
        RefundRequest refund = refundRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund request not found: " + id));

        refund.setItemsReceived(true);
        refund.setItemsReceivedAt(LocalDateTime.now());
        refund.setStatus(RefundStatus.ITEMS_RECEIVED);
        refund.setProcessedBy(receivedBy);
        refund.setUpdatedAt(LocalDateTime.now());

        return toDTO(refundRequestRepository.save(refund));
    }

    public long countPendingRefunds() {
        return refundRequestRepository.countByStatus(RefundStatus.PENDING);
    }

    private RefundRequestDTO toDTO(RefundRequest refund) {
        return RefundRequestDTO.builder()
                .id(refund.getId())
                .orderId(refund.getOrderId())
                .userId(refund.getUserId())
                .sellerId(refund.getSellerId())
                .type(refund.getType())
                .status(refund.getStatus())
                .productIds(refund.getProductIds())
                .quantities(refund.getQuantities())
                .reason(refund.getReason())
                .detailedDescription(refund.getDetailedDescription())
                .images(refund.getImages())
                .requestedAmount(refund.getRequestedAmount())
                .approvedAmount(refund.getApprovedAmount())
                .adminNotes(refund.getAdminNotes())
                .rejectionReason(refund.getRejectionReason())
                .trackingNumber(refund.getTrackingNumber())
                .itemsReceived(refund.getItemsReceived())
                .itemsReceivedAt(refund.getItemsReceivedAt())
                .processedBy(refund.getProcessedBy())
                .processedAt(refund.getProcessedAt())
                .createdAt(refund.getCreatedAt())
                .updatedAt(refund.getUpdatedAt())
                .build();
    }

    private PageResponse<RefundRequestDTO> toPageResponse(Page<RefundRequest> page) {
        return PageResponse.<RefundRequestDTO>builder()
                .content(page.getContent().stream().map(this::toDTO).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
