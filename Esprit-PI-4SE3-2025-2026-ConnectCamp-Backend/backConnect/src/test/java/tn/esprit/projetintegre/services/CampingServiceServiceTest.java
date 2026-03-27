package tn.esprit.projetintegre.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.projetintegre.entities.CampingService;
import tn.esprit.projetintegre.entities.User;
import tn.esprit.projetintegre.enums.Role;
import tn.esprit.projetintegre.enums.ServiceType;
import tn.esprit.projetintegre.exception.AccessDeniedException;
import tn.esprit.projetintegre.repositories.CampingServiceRepository;
import tn.esprit.projetintegre.repositories.EventServiceEntityRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;
import tn.esprit.projetintegre.repositories.UserRepository;
import tn.esprit.projetintegre.security.SecurityUtil;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampingServiceServiceTest {

    @Mock
    private CampingServiceRepository campingServiceRepository;
    @Mock
    private SiteRepository siteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventServiceEntityRepository eventServiceEntityRepository;

    @InjectMocks
    private CampingServiceService campingServiceService;

    private User providerUser;
    private CampingService serviceToCreate;
    private CampingService existingService;

    @BeforeEach
    void setUp() {
        providerUser = new User();
        providerUser.setId(10L);
        providerUser.setName("Service Provider");
        providerUser.setRole(Role.SELLER);

        serviceToCreate = new CampingService();
        serviceToCreate.setName("Tent Rental");
        serviceToCreate.setDescription("2-person tent");
        serviceToCreate.setType(ServiceType.ACCOMMODATION);
        serviceToCreate.setPrice(new BigDecimal("25.00"));
        
        existingService = new CampingService();
        existingService.setId(100L);
        existingService.setName("Tent Rental");
        existingService.setPrice(new BigDecimal("25.00"));
        existingService.setIsActive(true);
        existingService.setIsAvailable(true);
    }

    @Test
    void shouldCreateServiceSuccessfullyWhenAdmin() {
        Long providerId = 10L;
        Long siteId = null; // No site for this test

        when(userRepository.findById(providerId)).thenReturn(Optional.of(providerUser));
        when(campingServiceRepository.save(any(CampingService.class))).thenAnswer(invocation -> {
            CampingService saved = invocation.getArgument(0);
            saved.setId(50L);
            return saved;
        });

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.ADMIN)).thenReturn(true);

            CampingService result = campingServiceService.createService(serviceToCreate, providerId, siteId);

            assertNotNull(result);
            assertEquals(50L, result.getId());
            assertTrue(result.getIsActive());
            assertTrue(result.getIsAvailable());
            assertEquals(providerUser, result.getProvider());
            verify(campingServiceRepository, times(1)).save(any(CampingService.class));
        }
    }

    @Test
    void shouldThrowExceptionWhenNotAdminOnCreateService() {
        Long providerId = 10L;
        Long siteId = null;

        when(userRepository.findById(providerId)).thenReturn(Optional.of(providerUser));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.ADMIN)).thenReturn(false);

            assertThrows(AccessDeniedException.class, () -> {
                campingServiceService.createService(serviceToCreate, providerId, siteId);
            });
            verify(campingServiceRepository, never()).save(any(CampingService.class));
        }
    }

    @Test
    void shouldUpdateActiveStatusSuccessfully() {
        Long serviceId = 100L;
        CampingService updateDetails = new CampingService();
        updateDetails.setIsActive(false); // Disabling the service
        
        when(campingServiceRepository.findById(serviceId)).thenReturn(Optional.of(existingService));
        when(campingServiceRepository.save(any(CampingService.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.ADMIN)).thenReturn(true);

            CampingService result = campingServiceService.updateService(serviceId, updateDetails);

            assertNotNull(result);
            assertFalse(result.getIsActive()); // Service must be disabled now
            verify(campingServiceRepository, times(1)).save(any(CampingService.class));
        }
    }
}
