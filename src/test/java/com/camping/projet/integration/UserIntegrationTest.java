package com.camping.projet.integration;

import com.camping.projet.entity.User;
import com.camping.projet.enums.Role;
import com.camping.projet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = User.builder()
                .username("prof_test")
                .email("prof@esprit.tn")
                .password("encoded_password") // Simulate encoded password
                .role(Role.ROLE_USER)
                .nom("Test")
                .prenom("Prof")
                .telephone("12345678")
                .actif(true)
                .emailVerified(true)
                .nbReservations(0)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void shouldFetchUserFromDatabase() throws Exception {
        mockMvc.perform(get("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("prof_test"))
                .andExpect(jsonPath("$.email").value("prof@esprit.tn"));

        // Final verification: Ensure it was actually in the database
        assertTrue(userRepository.findById(testUser.getId()).isPresent());
    }
}
