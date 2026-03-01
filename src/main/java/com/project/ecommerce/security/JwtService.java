package com.project.ecommerce.security;

import com.project.ecommerce.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = new SecretKeySpec(signerKey.getBytes(), "HmacSHA256");
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String email = getEmailFromToken(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = getClaimFromToken(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
}
