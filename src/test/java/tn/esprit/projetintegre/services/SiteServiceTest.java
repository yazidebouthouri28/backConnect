package tn.esprit.projetintegre.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.projetintegre.dto.request.SiteRequest;
import tn.esprit.projetintegre.dto.response.SiteResponse;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.mapper.SiteModuleMapper;
import tn.esprit.projetintegre.repositories.SiteRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SiteServiceTest {

    @Mock
    private SiteRepository siteRepository;

    @Mock
    private SiteModuleMapper siteMapper;

    @InjectMocks
    private SiteService siteService;

    private Site site;
    private SiteRequest siteRequest;
    private SiteResponse siteResponse;

    @BeforeEach
    void setUp() {
        site = Site.builder()
                .id(1L)
                .name("Test Site")
                .city("Paris")
                .build();

        siteRequest = SiteRequest.builder()
                .name("Test Site")
                .city("Paris")
                .build();

        siteResponse = SiteResponse.builder()
                .id(1L)
                .name("Test Site")
                .city("Paris")
                .build();
    }

    @Test
    void testGetAllSites() {
        when(siteRepository.findAll()).thenReturn(Arrays.asList(site));
        when(siteMapper.toSiteResponseList(anyList())).thenReturn(Arrays.asList(siteResponse));

        List<SiteResponse> result = siteService.getAllSites();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Site", result.get(0).getName());
        verify(siteRepository, times(1)).findAll();
    }

    @Test
    void testGetSiteById_Success() {
        when(siteRepository.findById(1L)).thenReturn(Optional.of(site));
        when(siteMapper.toResponse(site)).thenReturn(siteResponse);

        SiteResponse result = siteService.getSiteById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Site", result.getName());
    }

    @Test
    void testGetSiteById_NotFound() {
        when(siteRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> siteService.getSiteById(2L));
        assertEquals("Site not found with id: 2", exception.getMessage());
    }

    @Test
    void testCreateSite() {
        when(siteMapper.toEntity(siteRequest)).thenReturn(site);
        when(siteRepository.save(site)).thenReturn(site);
        when(siteMapper.toResponse(site)).thenReturn(siteResponse);

        SiteResponse result = siteService.createSite(siteRequest);

        assertNotNull(result);
        assertEquals("Test Site", result.getName());
        verify(siteRepository, times(1)).save(site);
    }
}
