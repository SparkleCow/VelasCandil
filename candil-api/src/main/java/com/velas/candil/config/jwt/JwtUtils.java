package com.velas.candil.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.builder;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    private final JwtProperties jwtProperties;

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, Map.of());
    }

    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusMillis(jwtProperties.getExpiration());

        String token = builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationTime))
                .signWith(generateSignKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("JWT generated for user='{}', expiresAt='{}'",
                userDetails.getUsername(),
                expirationTime);

        return token;
    }

    public boolean validateToken(String token, UserDetails userDetails) {

        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();

        if (username == null) {
            log.warn("JWT validation failed: subject is null");
            return false;
        }

        if (!username.equals(userDetails.getUsername())) {
            log.warn("JWT validation failed: username mismatch token='{}' vs user='{}'",
                    username,
                    userDetails.getUsername());
            return false;
        }

        Date expiration = claims.getExpiration();

        if (expiration == null) {
            log.warn("JWT validation failed: expiration is null for user='{}'", username);
            return false;
        }

        if (expiration.before(new Date())) {
            log.warn("JWT expired for user='{}' at '{}'", username, expiration);
            return false;
        }

        log.debug("JWT valid for user='{}'", username);
        return true;
    }

    public String extractUsername(String token) {
        String username = extractClaim(token, Claims::getSubject);
        log.debug("Extracted username='{}' from JWT", username);
        return username;
    }

    public boolean isExpired(String token) {
        try {
            Date exp = extractClaim(token, Claims::getExpiration);
            boolean expired = exp == null || exp.before(new Date());

            if (expired) {
                log.warn("JWT is expired at '{}'", exp);
            } else {
                log.debug("JWT still valid until '{}'", exp);
            }

            return expired;
        } catch (JwtException ex) {
            log.warn("Failed to check expiration: {}", ex.getMessage());
            return true;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        T value = claimsResolver.apply(claims);

        log.debug("Extracted claim '{}' from JWT",
                claimsResolver.getClass().getSimpleName());

        return value;
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(generateSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key generateSignKey() {
        String secret = jwtProperties.getSecretKey();
        byte[] keyBytes;

        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ex) {
            log.debug("JWT secret is not base64 encoded, using raw bytes");
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}