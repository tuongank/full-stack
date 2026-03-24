package com.project.ecommerce.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    /**
     * Sends an HTML email. Falls back gracefully if sending fails.
     */
    public void sendAnEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from != null ? from : "");
            helper.setTo(to != null ? to : "");
            helper.setSubject(subject != null ? subject : "");
            // body is treated as HTML if it starts with '<', otherwise plain text
            boolean isHtml = body != null && body.trim().startsWith("<");
            helper.setText(body != null ? body : "", isHtml);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
