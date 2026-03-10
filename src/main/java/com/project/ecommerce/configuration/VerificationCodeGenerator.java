package com.project.ecommerce.configuration;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class VerificationCodeGenerator {
    private final SecureRandom random = new SecureRandom();

    public String generateVerificationCode(int digits) {
        int bound = (int)Math.pow(10, digits);
        int code = random.nextInt(bound);
        return String.format("%0" + digits + "d", code);
    }
}
