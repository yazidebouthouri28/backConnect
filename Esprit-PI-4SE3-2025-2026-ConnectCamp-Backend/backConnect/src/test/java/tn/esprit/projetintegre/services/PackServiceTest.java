package tn.esprit.projetintegre.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.projetintegre.dto.PackDTO;
import tn.esprit.projetintegre.entities.CampingService;
import tn.esprit.projetintegre.entities.Pack;
import tn.esprit.projetintegre.enums.PackType;
import tn.esprit.projetintegre.enums.Role;
import tn.esprit.projetintegre.exception.BusinessException;
import tn.esprit.projetintegre.repositories.CampingServiceRepository;
import tn.esprit.projetintegre.repositories.PackRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;
import tn.esprit.projetintegre.security.SecurityUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PackServiceTest {

    @Mock
    private PackRepository packRepository;
    @Mock
    private SiteRepository siteRepository;
    @Mock
    private CampingServiceRepository campingServiceRepository;

    @InjectMocks
    private PackService packService;

    private PackDTO.CreateRequest createRequest;
    private CampingService service1;
    private CampingService service2;

    @BeforeEach
    void setUp() {
        createRequest = new PackDTO.CreateRequest();
        createRequest.setName("Weekend Getaway");
        createRequest.setPackType(PackType.FAMILY);
        createRequest.setPrice(new BigDecimal("150.00")); // Discounted price
        createRequest.setServiceIds(Arrays.asList(1L, 2L));

        service1 = new CampingService();
        service1.setId(1L);
        service1.setName("Tent Rental");
        service1.setPrice(new BigDecimal("100.00"));

        service2 = new CampingService();
        service2.setId(2L);
        service2.setName("Food Package");
        service2.setPrice(new BigDecimal("100.00"));
    }

    @Test
    void shouldCalculatePackPriceWithDiscountSuccessfully() {
        when(campingServiceRepository.findAllById(anyList())).thenReturn(Arrays.asList(service1, service2));
        when(packRepository.save(any(Pack.class))).thenAnswer(invocation -> {
            Pack pack = invocation.getArgument(0);
            pack.setId(10L);
            return pack;
        });

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.ADMIN)).thenReturn(true);

            PackDTO.Response response = packService.createPack(createRequest);

            // Total original price should be 100 + 100 = 200. Discounted is 150.
            assertNotNull(response);
            assertEquals(10L, response.getId());
            assertEquals(new BigDecimal("200.00"), response.getOriginalPrice());
            assertEquals(25.0, response.getDiscountPercentage()); // 50 / 200 = 25%
            assertEquals(2, response.getServiceIds().size());
            verify(packRepository, times(1)).save(any(Pack.class));
        }
    }

    @Test
    void shouldThrowExceptionWhenPackPriceIsHigherThanOriginalPrice() {
        // Overprice the pack
        createRequest.setPrice(new BigDecimal("300.00"));

        when(campingServiceRepository.findAllById(anyList())).thenReturn(Arrays.asList(service1, service2));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.hasRole(Role.ADMIN)).thenReturn(true);

            Exception exception = assertThrows(BusinessException.class, () -> {
                packService.createPack(createRequest);
            });

            assertTrue(exception.getMessage().contains("doit être inférieur au prix normal cumulé"));
            verify(packRepository, never()).save(any(Pack.class));
        }
    }
}
