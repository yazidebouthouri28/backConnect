package tn.esprit.projetPi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // SWAGGER UI - CHEMINS COMPLETS
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/webjars/**",
                                "/webjars/swagger-ui/**",
                                "/webjars/swagger-ui/5.18.2/**"  // AJOUT SPÉCIFIQUE
                        ).permitAll()

                        // Public endpoints - Authentication
                        .requestMatchers("/api/auth/**").permitAll()

                        // Public endpoints - Products & Categories (E-commerce)
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

                        // Public endpoints - Campsites
                        .requestMatchers(HttpMethod.GET, "/api/campsites").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/campsites/featured").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/campsites/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/campsites/map").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/campsites/search").permitAll()

                        // Public endpoints - Events
                        .requestMatchers(HttpMethod.GET, "/api/events").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events/featured").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events/upcoming").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/events/search").permitAll()

                        // Public endpoints - Reviews
                        .requestMatchers(HttpMethod.GET, "/api/reviews/target/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/campsite/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/event/**").permitAll()

                        // Public endpoints - Sponsors
                        .requestMatchers(HttpMethod.GET, "/api/sponsors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sponsors/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sponsors/event/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sponsors/tier/**").permitAll()

                        // Public endpoints - Chat rooms (public only)
                        .requestMatchers(HttpMethod.GET, "/api/messages/rooms/public").permitAll()

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:8089"  // Ajout pour Swagger UI
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}