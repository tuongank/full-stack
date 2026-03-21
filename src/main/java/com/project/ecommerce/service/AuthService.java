package com.project.ecommerce.service;

import com.project.ecommerce.configuration.MailConfiguration;
import com.project.ecommerce.configuration.VerificationCodeGenerator;
import com.project.ecommerce.dto.request.AuthRequest;
import com.project.ecommerce.dto.request.RegisterRequest;
import com.project.ecommerce.dto.request.ResetPasswordRequest;
import com.project.ecommerce.dto.request.VerifyCodeRequest;
import com.project.ecommerce.dto.response.AuthResponse;
import com.project.ecommerce.dto.response.RegisterResponse;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.UserRole;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailConfiguration  mailConfiguration;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final UserProfileService userProfileService;

    @Value("${app.verification.code.length}")
    private int codeLength;

    @Value("${app.verification.expiry.minutes}")
    private Long expiryMinutes;

    @Value("${app.verification.resend.cooldown.seconds}")
    private Long resendCode;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Email is incorrect"));

        // Check if the password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new NotFoundException("Password is incorrect");
        }

        // Check if the account is verified
        if (!Boolean.TRUE.equals(user.getVerified())) {
            throw new IllegalStateException("Account not verified. Please verify your account before logging in.");
        }

        // Generate JWT token
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
        userProfileService.createDefaultProfileIfAbsent(user);

        try {
            // Send verification email
            mailConfiguration.sendVerificationMail(user.getEmail(), code, expiryMinutes);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return RegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public void verifyRegistrationCode(VerifyCodeRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("Email is incorrect"));

        if (Boolean.TRUE.equals(user.getVerified())) {
            throw new IllegalStateException("User already verified");
        }

        if (user.getVerificationAttempts() != null && user.getVerificationAttempts() >= 5) {
            throw new IllegalStateException("Too many failed attempts. Please request a new code.");
        }

        if (user.getVerificationCode() == null || user.getVerificationExpiry() == null) {
            throw new IllegalStateException("No verification code");
        }

        if (LocalDateTime.now().isAfter(user.getVerificationExpiry())) {
            throw new IllegalStateException("Verification code expired");
        }

        if (!user.getVerificationCode().equals(request.getCode())) {
            int attempts = user.getVerificationAttempts() != null ? user.getVerificationAttempts() : 0;
            user.setVerificationAttempts(attempts + 1);
            userRepository.save(user);
            throw new IllegalStateException("Invalid verification code");
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        user.setVerificationAttempts(0);
        userRepository.save(user);
    }

    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        if (Boolean.TRUE.equals(user.getVerified())) {
            throw new IllegalStateException("Account already verified");
        }

        if (user.getLastVerificationSentAt() != null) {
            long passed = Duration.between(user.getLastVerificationSentAt(), LocalDateTime.now()).getSeconds();
            if (passed < resendCode) {
                long wait = resendCode - passed;
                throw new IllegalStateException("Please try again in " + wait + " seconds");
            }
        }

        String code = verificationCodeGenerator.generateVerificationCode(codeLength);
        user.setVerificationCode(code);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(expiryMinutes));
        user.setLastVerificationSentAt(LocalDateTime.now());
        user.setVerificationAttempts(0);
        userRepository.save(user);

        mailConfiguration.sendVerificationMail(user.getEmail(), code, expiryMinutes);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        String code = verificationCodeGenerator.generateVerificationCode(codeLength);
        user.setVerificationCode(code);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(expiryMinutes));
        user.setVerificationAttempts(0);

        mailConfiguration.sendVerificationMail(user.getEmail(), code, expiryMinutes);
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + request.getEmail()));

        if (user.getVerificationAttempts() != null && user.getVerificationAttempts() >= 5) {
            throw new IllegalStateException("Too many failed attempts. Please request a new code.");
        }

        if (user.getVerificationCode() == null || user.getVerificationExpiry() == null) {
            throw new IllegalStateException("No verification code");
        }

        if (LocalDateTime.now().isAfter(user.getVerificationExpiry())) {
            throw new IllegalStateException("Verification code expired");
        }

        if (!user.getVerificationCode().equals(request.getCode())) {
            user.setVerificationAttempts(user.getVerificationAttempts() + 1);
            throw new IllegalStateException("Mã xác thực không chính xác");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        user.setVerificationAttempts(0);
        userRepository.save(user);

    }

}
