package tn.esprit.projetPi.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDashboardDTO {
    // User stats
    private Long totalUsers;
    private Long activeUsers;
    private Long newUsersThisMonth;
    private Map<String, Long> usersByRole;
    
    // Campsite stats
    private Long totalCampsites;
    private Long activeCampsites;
    private Long featuredCampsites;
    
    // Event stats
    private Long totalEvents;
    private Long upcomingEvents;
    private Long activeEvents;
    
    // Reservation stats
    private Long totalReservations;
    private Long pendingReservations;
    private Long confirmedReservations;
    private Long completedReservations;
    private Long cancelledReservations;
    
    // Revenue stats
    private Double totalRevenue;
    private Double revenueThisMonth;
    private Double averageOrderValue;
    
    // Review stats
    private Long totalReviews;
    private Long pendingReviews;
    private Long reportedReviews;
    private Double averageRating;
    
    // Message stats
    private Long totalMessages;
    private Long messagesThisWeek;
    
    // Sponsor stats
    private Long totalSponsors;
    private Long activeSponsors;
}
