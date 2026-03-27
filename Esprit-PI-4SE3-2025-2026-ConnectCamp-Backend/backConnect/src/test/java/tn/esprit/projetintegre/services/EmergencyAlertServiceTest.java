package tn.esprit.projetintegre.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.projetintegre.dto.EmergencyAlertDTO;
import tn.esprit.projetintegre.entities.EmergencyAlert;
import tn.esprit.projetintegre.entities.User;
import tn.esprit.projetintegre.enums.AlertStatus;
import tn.esprit.projetintegre.enums.EmergencySeverity;
import tn.esprit.projetintegre.enums.Role;
import tn.esprit.projetintegre.exception.AccessDeniedException;
import tn.esprit.projetintegre.repositories.EmergencyAlertRepository;
import tn.esprit.projetintegre.repositories.EventRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;
import tn.esprit.projetintegre.repositories.UserRepository;
import tn.esprit.projetintegre.security.SecurityUtil;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmergencyAlertServiceTest {

    @Mock
    private EmergencyAlertRepository alertRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SiteRepository siteRepository;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EmergencyAlertService emergencyAlertService;

    private User camperUser;
    private User adminUser;
    private EmergencyAlertDTO.CreateRequest createRequest;
    private EmergencyAlert savedAlert;

    @BeforeEach
    void setUp() {
        camperUser = new User();
        camperUser.setId(1L);
        camperUser.setName("Test Camper");
        camperUser.setRole(Role.CAMPER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setName("Test Admin");
        adminUser.setRole(Role.ADMIN);

        createRequest = new EmergencyAlertDTO.CreateRequest();
        createRequest.setTitle("Need Help");
        createRequest.setDescription("Lost in the woods");
        createRequest.setSeverity(EmergencySeverity.MEDIUM);
        createRequest.setLatitude(36.8);
        createRequest.setLongitude(10.1);
        createRequest.setLocation("Tunis");

        savedAlert = new EmergencyAlert();
        savedAlert.setId(100L);
        savedAlert.setTitle(createRequest.getTitle());
        savedAlert.setDescription(createRequest.getDescription());
        savedAlert.setStatus(AlertStatus.ACTIVE);
        savedAlert.setReportedBy(camperUser);
        savedAlert.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateAlertSuccessfully() {
        // Arrange
        Long reporterId = 1L;
        when(userRepository.findById(reporterId)).thenReturn(Optional.of(camperUser));
        when(alertRepository.save(any(EmergencyAlert.class))).thenReturn(savedAlert);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.CAMPER)).thenReturn(true);
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.PARTICIPANT)).thenReturn(false);
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.USER)).thenReturn(false);

            // Act
            EmergencyAlertDTO.Response response = emergencyAlertService.createAlert(reporterId, createRequest);

            // Assert
            assertNotNull(response);
            assertEquals(100L, response.getId());
            assertEquals("Need Help", response.getTitle());
            assertEquals(AlertStatus.ACTIVE, response.getStatus());
            verify(alertRepository, times(1)).save(any(EmergencyAlert.class));
        }
    }

    @Test
    void shouldThrowExceptionWhenUserNotCamperOnCreate() {
        // Arrange
        Long reporterId = 1L;

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.CAMPER)).thenReturn(false);
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.PARTICIPANT)).thenReturn(false);
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.USER)).thenReturn(false);

            // Act & Assert
            assertThrows(AccessDeniedException.class, () -> {
                emergencyAlertService.createAlert(reporterId, createRequest);
            });
            verify(alertRepository, never()).save(any(EmergencyAlert.class));
        }
    }

    @Test
    void shouldUpdateAlertStatusToAcknowledged() {
        // Arrange
        Long alertId = 100L;
        Long adminId = 2L;

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(savedAlert));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(alertRepository.save(any(EmergencyAlert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.ADMIN)).thenReturn(true);

            // Act
            EmergencyAlertDTO.Response response = emergencyAlertService.acknowledgeAlert(alertId, adminId);

            // Assert
            assertNotNull(response);
            assertEquals(AlertStatus.ACKNOWLEDGED, response.getStatus());
            assertNotNull(response.getAcknowledgedAt());
            assertEquals(adminUser.getName(), response.getAcknowledgedByName());
            verify(alertRepository, times(1)).save(any(EmergencyAlert.class));
        }
    }
}
