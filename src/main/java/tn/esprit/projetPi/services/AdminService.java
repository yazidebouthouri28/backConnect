package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.AdminDashboardDTO;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.repositories.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CampsiteRepository campsiteRepository;
    private final ReservationRepository reservationRepository;


    public AdminDashboardDTO getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekStart = now.minusDays(7);
        
        // User stats
        List<User> allUsers = userRepository.findAll();
        long totalUsers = allUsers.size();
        long activeUsers = allUsers.stream().filter(u -> Boolean.TRUE.equals(u.getIsActive())).count();
        long newUsersThisMonth = allUsers.stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(monthStart))
                .count();
        
        Map<String, Long> usersByRole = allUsers.stream()
                .filter(u -> u.getRole() != null)
                .collect(Collectors.groupingBy(u -> u.getRole().name(), Collectors.counting()));
        
        // Campsite stats
        List<Campsite> allCampsites = campsiteRepository.findAll();
        long totalCampsites = allCampsites.size();
        long activeCampsites = allCampsites.stream().filter(c -> Boolean.TRUE.equals(c.getIsActive())).count();
        long featuredCampsites = allCampsites.stream().filter(c -> Boolean.TRUE.equals(c.getIsFeatured())).count();

        // Event stats



        // Reservation stats
        List<Reservation> allReservations = reservationRepository.findAll();
        long totalReservations = allReservations.size();
        long pendingReservations = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING).count();
        long confirmedReservations = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED).count();
        long completedReservations = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.COMPLETED).count();
        long cancelledReservations = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED).count();

        // Revenue stats
        double totalRevenue = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.COMPLETED || r.getStatus() == ReservationStatus.CONFIRMED)
                .mapToDouble(r -> r.getTotalPrice() != null ? r.getTotalPrice() : 0)
                .sum();
        
        double revenueThisMonth = allReservations.stream()
                .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().isAfter(monthStart))
                .filter(r -> r.getStatus() == ReservationStatus.COMPLETED || r.getStatus() == ReservationStatus.CONFIRMED)
                .mapToDouble(r -> r.getTotalPrice() != null ? r.getTotalPrice() : 0)
                .sum();
        
        double averageOrderValue = totalReservations > 0 ? totalRevenue / totalReservations : 0;
        
        // Review stats

        // Message stats


        return AdminDashboardDTO.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .usersByRole(usersByRole)
                .totalCampsites(totalCampsites)
                .activeCampsites(activeCampsites)
                .featuredCampsites(featuredCampsites)

                .totalReservations(totalReservations)
                .pendingReservations(pendingReservations)
                .confirmedReservations(confirmedReservations)
                .completedReservations(completedReservations)
                .cancelledReservations(cancelledReservations)
                .totalRevenue(totalRevenue)
                .revenueThisMonth(revenueThisMonth)
                .averageOrderValue(averageOrderValue)

                .build();
    }

    public void updateUserRole(String userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    public void toggleUserActive(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        userRepository.save(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
