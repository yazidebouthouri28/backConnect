package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.projetintegre.dto.AuthRequest;
import tn.esprit.projetintegre.dto.AuthResponse;
import tn.esprit.projetintegre.dto.RegisterRequest;
import tn.esprit.projetintegre.entities.Cart;
import tn.esprit.projetintegre.entities.Organizer;
import tn.esprit.projetintegre.entities.User;
import tn.esprit.projetintegre.entities.Wallet;
import tn.esprit.projetintegre.enums.Role;
import tn.esprit.projetintegre.exception.DuplicateResourceException;
import tn.esprit.projetintegre.repositories.CartRepository;
import tn.esprit.projetintegre.repositories.OrganizerRepository;
import tn.esprit.projetintegre.repositories.UserRepository;
import tn.esprit.projetintegre.repositories.WalletRepository;
import tn.esprit.projetintegre.security.JwtTokenProvider;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final WalletRepository walletRepository;
    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Role role = Role.CLIENT; // Default role
        if (request.getRole() != null) {
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Keep default or map specific frontend roles
                if ("USER".equalsIgnoreCase(request.getRole()))
                    role = Role.USER;
            }
        }

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(role)
                .isActive(true)
                .isSuspended(false)
                .isBuyer(true)
                .isSeller(role == Role.SELLER)
                .isAdmin(false)
                .loyaltyPoints(0)
                .loyaltyTier("BRONZE")
                .experiencePoints(0)
                .level(1)
                .build();

        user = userRepository.save(user);

        // Create cart for user
        Cart cart = Cart.builder().user(user).build();
        cartRepository.save(cart);

        // Create wallet for user
        Wallet wallet = Wallet.builder().user(user).build();
        walletRepository.save(wallet);

        String token = jwtTokenProvider.generateToken(user.getUsername());

        Long organizerId = null;
        if (role == Role.ORGANIZER) {
            Organizer organizer = Organizer.builder()
                    .user(user)
                    .companyName(user.getName() + " Events")
                    .verified(false)
                    .active(true)
                    .totalEvents(0)
                    .rating(java.math.BigDecimal.ZERO)
                    .build();
            organizer = organizerRepository.save(organizer);
            organizerId = organizer.getId();
        }

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .organizerId(organizerId)
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(authentication);

        Long organizerId = null;
        if (user.getRole() == Role.ORGANIZER) {
            Organizer organizer = organizerRepository.findByUser_Id(user.getId())
                    .orElseGet(() -> {
                        Organizer newOrg = Organizer.builder()
                                .user(user)
                                .companyName(user.getName() + " Events")
                                .verified(false)
                                .active(true)
                                .totalEvents(0)
                                .rating(java.math.BigDecimal.ZERO)
                                .build();
                        return organizerRepository.save(newOrg);
                    });
            organizerId = organizer.getId();
        }

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .organizerId(organizerId)
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
