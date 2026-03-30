package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import tn.esprit.projetintegre.exception.AccessDeniedException;
import tn.esprit.projetintegre.security.SecurityUtil;
import tn.esprit.projetintegre.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.projetintegre.entities.Alert;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.entities.User;
import tn.esprit.projetintegre.enums.AlertStatus;
import tn.esprit.projetintegre.exception.ResourceNotFoundException;
import tn.esprit.projetintegre.repositories.AlertRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;
import tn.esprit.projetintegre.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

    private final AlertRepository alertRepository;
    private final SiteRepository siteRepository;
    private final UserRepository userRepository;

    public List<Alert> getAllAlerts() {
        // Only ADMIN can view all alerts
        if (!SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can view all alerts");
        }
        return alertRepository.findAll();
    }

    public Page<Alert> getAllAlerts(Pageable pageable) {
        // Only ADMIN can view all alerts with pagination
        if (!SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can view all alerts");
        }
        return alertRepository.findAll(pageable);
    }

    public Alert getAlertById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
    }

    public List<Alert> getAlertsByStatus(AlertStatus status) {
        return alertRepository.findByStatus(status);
    }

    public Page<Alert> getAlertsBySiteId(Long siteId, Pageable pageable) {
        return alertRepository.findBySiteId(siteId, pageable);
    }

    public Alert createAlert(Alert alert, Long reportedById, Long siteId) {
        // CAMPPER and ADMIN can create alerts
        if (!SecurityUtil.hasRole(Role.CAMPER) && !SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only CAMPPER or ADMIN can create alerts");
        }
        User reporter = userRepository.findById(reportedById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reportedById));
        alert.setReportedBy(reporter);

        if (siteId != null) {
            Site site = siteRepository.findById(siteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Site not found with id: " + siteId));
            alert.setSite(site);
        }

        alert.setStatus(AlertStatus.ACTIVE);
        alert.setReportedAt(LocalDateTime.now());
        return alertRepository.save(alert);
    }

    public Alert updateAlert(Long id, Alert alertDetails) {
        // Only ADMIN can update alerts
        if (!SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can update alerts");
        }
        Alert alert = getAlertById(id);
        alert.setTitle(alertDetails.getTitle());
        alert.setDescription(alertDetails.getDescription());
        alert.setAlertType(alertDetails.getAlertType());
        alert.setSeverity(alertDetails.getSeverity());
        alert.setLocation(alertDetails.getLocation());
        alert.setLatitude(alertDetails.getLatitude());
        alert.setLongitude(alertDetails.getLongitude());
        return alertRepository.save(alert);
    }

    public Alert resolveAlert(Long id, Long resolvedById, String resolutionNotes) {
        // Only ADMIN can resolve alerts
        if (!SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can resolve alerts");
        }
        Alert alert = getAlertById(id);
        User resolver = userRepository.findById(resolvedById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + resolvedById));

        alert.setStatus(AlertStatus.RESOLVED);
        alert.setResolvedBy(resolver);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolutionNotes(resolutionNotes);
        return alertRepository.save(alert);
    }

    public Alert updateStatus(Long id, AlertStatus status) {
        // Only ADMIN can update alert status
        if (!SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can update alert status");
        }
        Alert alert = getAlertById(id);
        alert.setStatus(status);
        return alertRepository.save(alert);
    }

    public void deleteAlert(Long id) {
        // Only ADMIN can delete alerts
        if (!SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can delete alerts");
        }
        Alert alert = getAlertById(id);
        alertRepository.delete(alert);
    }
}
