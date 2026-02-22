package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.CreateRentalRequest;
import tn.esprit.projetPi.dto.RentalDTO;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.RentalRepository;
import tn.esprit.projetPi.repositories.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ProductRepository productRepository;

    public List<RentalDTO> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RentalDTO getRentalById(String id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with id: " + id));
        return toDTO(rental);
    }

    public List<RentalDTO> getRentalsByUser(String userId) {
        return rentalRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<RentalDTO> getActiveRentals() {
        return rentalRepository.findByStatus(RentalStatus.ACTIVE).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<RentalDTO> getOverdueRentals() {
        return rentalRepository.findOverdueRentals(LocalDateTime.now()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<RentalDTO> getRentalsEndingSoon(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusDays(days);
        return rentalRepository.findRentalsEndingSoon(now, end).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RentalDTO createRental(String userId, CreateRentalRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));

        if (!Boolean.TRUE.equals(product.getRentalAvailable())) {
            throw new IllegalStateException("Product is not available for rental");
        }

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        BigDecimal rentalPrice = product.getRentalPricePerDay() != null ? product.getRentalPricePerDay() : product.getPrice();
        BigDecimal totalCost = rentalPrice.multiply(BigDecimal.valueOf(days));
        BigDecimal deposit = totalCost.multiply(BigDecimal.valueOf(0.2)); // 20% deposit

        Rental rental = new Rental();
        rental.setProductId(request.getProductId());
        rental.setProductName(product.getName());
        rental.setUserId(userId);
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setDaysLeft((int) days);
        rental.setStatus(RentalStatus.ACTIVE);
        rental.setTotalCost(totalCost);
        rental.setDepositAmount(deposit);
        rental.setDepositReturned(false);

        Rental saved = rentalRepository.save(rental);
        return toDTO(saved);
    }

    public RentalDTO extendRental(String id, int additionalDays) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with id: " + id));

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalStateException("Can only extend active rentals");
        }

        Product product = productRepository.findById(rental.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        BigDecimal rentalPrice = product.getRentalPricePerDay() != null ? product.getRentalPricePerDay() : product.getPrice();
        BigDecimal additionalCost = rentalPrice.multiply(BigDecimal.valueOf(additionalDays));

        rental.setEndDate(rental.getEndDate().plusDays(additionalDays));
        rental.setDaysLeft(rental.getDaysLeft() + additionalDays);
        rental.setTotalCost(rental.getTotalCost().add(additionalCost));

        Rental saved = rentalRepository.save(rental);
        return toDTO(saved);
    }

    public RentalDTO returnRental(String id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with id: " + id));

        if (rental.getStatus() == RentalStatus.COMPLETED) {
            throw new IllegalStateException("Rental has already been returned");
        }

        rental.setStatus(RentalStatus.COMPLETED);
        rental.setDepositReturned(true);
        rental.setDaysLeft(0);

        Rental saved = rentalRepository.save(rental);
        return toDTO(saved);
    }

    public RentalDTO cancelRental(String id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with id: " + id));

        if (rental.getStatus() == RentalStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed rental");
        }

        rental.setStatus(RentalStatus.CANCELLED);
        rental.setDepositReturned(true);

        Rental saved = rentalRepository.save(rental);
        return toDTO(saved);
    }

    public void updateOverdueRentals() {
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(LocalDateTime.now());
        for (Rental rental : overdueRentals) {
            rental.setStatus(RentalStatus.OVERDUE);
            long overdueDays = ChronoUnit.DAYS.between(rental.getEndDate(), LocalDateTime.now());
            rental.setDaysLeft((int) -overdueDays);
            rentalRepository.save(rental);
        }
    }

    private RentalDTO toDTO(Rental rental) {
        RentalDTO dto = new RentalDTO();
        dto.setId(rental.getId());
        dto.setProductId(rental.getProductId());
        dto.setProductName(rental.getProductName());
        dto.setUserId(rental.getUserId());
        dto.setStartDate(rental.getStartDate());
        dto.setEndDate(rental.getEndDate());
        dto.setDaysLeft(rental.getDaysLeft());
        dto.setStatus(rental.getStatus());
        dto.setTotalCost(rental.getTotalCost());
        dto.setDepositAmount(rental.getDepositAmount());
        dto.setDepositReturned(rental.getDepositReturned());
        return dto;
    }
}
