package com.project.ecommerce.service;

import com.project.ecommerce.configuration.MailConfiguration;
import com.project.ecommerce.configuration.VerificationCodeGenerator;
import com.project.ecommerce.dto.request.AuthRequest;
import com.project.ecommerce.dto.request.RegisterRequest;
import com.project.ecommerce.dto.response.AuthResponse;
import com.project.ecommerce.dto.response.RegisterResponse;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.UserRole;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailConfiguration  mailConfiguration;
    private final VerificationCodeGenerator  verificationCodeGenerator;

    @Value("${app.verification.code.length}")
    private int codeLength;

    @Value("${app.verification.expiry.minutes}")
    private Long expiryMinutes;

    @Value("${app.verification.resend.cooldown.seconds}")
    private Long resendCode;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("Email is incorrect"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new NotFoundException("Password is incorrect");
        }
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new NotFoundException("Email already exists");
        }

        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new NotFoundException("Phone already exists");
        }
        // verify account
        String code = verificationCodeGenerator.generateVerificationCode(codeLength);

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .verificationCode(code)
                .verificationExpiry(LocalDateTime.now().plusMinutes(expiryMinutes))
                .lastVerificationSentAt(LocalDateTime.now())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();
        userRepository.save(user);

        mailConfiguration.sendVerificationMail(user.getEmail(), code, expiryMinutes);

        return RegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    @Transactional
    public void  verifyRegistrationCode(String email, String code) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Email is incorrect"));

        if (Boolean.TRUE.equals(user.getVerified())) {
            throw new IllegalStateException("User already verified");
        }

        if (user.getVerificationCode() == null || user.getVerificationExpiry() == null) {
            throw new IllegalStateException("No verification code");
        }

        if (LocalDateTime.now().isAfter(user.getVerificationExpiry())) {
            throw new IllegalStateException("Verification code expired");
        }

        if (!user.getVerificationCode().equals(code)) {
            user.setVerificationAttempts(user.getVerificationAttempts() + 1);
            userRepository.save(user);
            throw new IllegalStateException("Invalid verification code");
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        user.setVerificationAttempts(0);
        userRepository.save(user);
    }

    @Transactional
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        if (Boolean.TRUE.equals(user.getVerified())) {
            throw new IllegalStateException("Account already verified");
        }

        if (user.getLastVerificationSentAt() != null &&
                Duration.between(user.getLastVerificationSentAt(), LocalDateTime.now()).getSeconds() < resendCode) {
            long wait = resendCode - Duration.between(user.getLastVerificationSentAt(), LocalDateTime.now()).getSeconds();
            throw new IllegalStateException("Please try again in " + wait + " seconds");
        }

        String code = verificationCodeGenerator.generateVerificationCode(codeLength);
        user.setVerificationCode(code);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(expiryMinutes));
        user.setLastVerificationSentAt(LocalDateTime.now());
        userRepository.save(user);

        mailConfiguration.sendVerificationMail(user.getEmail(), code, expiryMinutes);
    }
}
