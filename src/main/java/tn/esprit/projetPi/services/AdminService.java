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
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final MessageRepository messageRepository;
    private final SponsorRepository sponsorRepository;

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
        List<Event> allEvents = eventRepository.findAll();
        long totalEvents = allEvents.size();
        long upcomingEvents = allEvents.stream()
                .filter(e -> e.getStartDate() != null && e.getStartDate().isAfter(now))
                .count();
        long activeEvents = allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.REGISTRATION_OPEN || e.getStatus() == EventStatus.IN_PROGRESS)
                .count();
        
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
        List<Review> allReviews = reviewRepository.findAll();
        long totalReviews = allReviews.size();
        long pendingReviews = allReviews.stream().filter(r -> !Boolean.TRUE.equals(r.getIsApproved())).count();
        long reportedReviews = allReviews.stream().filter(r -> Boolean.TRUE.equals(r.getIsReported())).count();
        double averageRating = allReviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
        
        // Message stats
        List<Message> allMessages = messageRepository.findAll();
        long totalMessages = allMessages.size();
        long messagesThisWeek = allMessages.stream()
                .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(weekStart))
                .count();
        
        // Sponsor stats
        List<Sponsor> allSponsors = sponsorRepository.findAll();
        long totalSponsors = allSponsors.size();
        long activeSponsors = allSponsors.stream().filter(s -> Boolean.TRUE.equals(s.getIsActive())).count();
        
        return AdminDashboardDTO.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .usersByRole(usersByRole)
                .totalCampsites(totalCampsites)
                .activeCampsites(activeCampsites)
                .featuredCampsites(featuredCampsites)
                .totalEvents(totalEvents)
                .upcomingEvents(upcomingEvents)
                .activeEvents(activeEvents)
                .totalReservations(totalReservations)
                .pendingReservations(pendingReservations)
                .confirmedReservations(confirmedReservations)
                .completedReservations(completedReservations)
                .cancelledReservations(cancelledReservations)
                .totalRevenue(totalRevenue)
                .revenueThisMonth(revenueThisMonth)
                .averageOrderValue(averageOrderValue)
                .totalReviews(totalReviews)
                .pendingReviews(pendingReviews)
                .reportedReviews(reportedReviews)
                .averageRating(averageRating)
                .totalMessages(totalMessages)
                .messagesThisWeek(messagesThisWeek)
                .totalSponsors(totalSponsors)
                .activeSponsors(activeSponsors)
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
