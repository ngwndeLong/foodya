package com.foodya.foodya_backend.auth.service;

import com.foodya.foodya_backend.auth.dto.AuthResponse;
import com.foodya.foodya_backend.auth.dto.LoginRequest;
import com.foodya.foodya_backend.auth.dto.RegisterRequest;
import com.foodya.foodya_backend.auth.dto.RefreshTokenRequest;
import com.foodya.foodya_backend.jwt.JwtService;
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.model.User.Role;
import com.foodya.foodya_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        user.setIsActive(true);
        user.setIsEmailVerified(false);

        // Generate email verification token
        user.setEmailVerificationToken(UUID.randomUUID().toString());
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        // Save user
        User savedUser = userRepository.save(user);

        // Generate tokens
        UserDetails userDetails = createUserDetails(savedUser);
        String accessToken = jwtService.generateToken(createExtraClaims(savedUser), userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // TODO: Send verification email

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpirationTime())
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Get user from database
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if account is active
        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        UserDetails userDetails = createUserDetails(user);
        String accessToken = jwtService.generateToken(createExtraClaims(user), userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpirationTime())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Extract username from token
        String username = jwtService.extractUsername(refreshToken);

        // Get user from database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if account is active
        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // Generate new tokens
        UserDetails userDetails = createUserDetails(user);
        String newAccessToken = jwtService.generateToken(createExtraClaims(user), userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpirationTime())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (user.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        user.setIsEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // TODO: Send password reset email
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
    }

    // Helper method to create UserDetails
    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .accountExpired(false)
                .accountLocked(!user.isActive())
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }

    // Helper method to create extra claims for JWT
    private Map<String, Object> createExtraClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("fullName", user.getFullName());
        return claims;
    }
}
