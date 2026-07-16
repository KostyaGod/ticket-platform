package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.enums.Role;
import dev.kostya.ticketplatform.auth.security.JwtProperties;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JwtProperties properties;
    private final SessionService sessionService;
    private SecretKey accessKey;
    private SecretKey refreshKey;

    @PostConstruct
    public void init() {
        getAccessKey();
        getRefreshKey();
    }

    @Override
    public String generateAccessToken(UserPrincipal userPrincipal, UUID sessionId) {
        return Jwts.builder()
                .subject(String.valueOf(userPrincipal.getId()))
                .claim("role", userPrincipal.getRole())
                .claim("session", sessionId)
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + properties.getAccessExpiration()
                ))
                .signWith(accessKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserPrincipal userPrincipal, UUID sessionId, UUID jti) {
        return Jwts.builder()
                .subject(String.valueOf(userPrincipal.getId()))
                .claim("jti", jti)
                .claim("session", sessionId)
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + properties.getRefreshExpiration()
                ))
                .signWith(refreshKey)
                .compact();
    }

    @Override
    public boolean validateAccess(String token) {
        try {
            extractClaimsAccess(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean validateRefresh(String token) {
        try {
            extractClaimsRefresh(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Long extractUserIdAccess(String token) {
        return Long.valueOf(
                extractClaimsAccess(token).getSubject()
        );
    }

    @Override
    public Long extractUserIdRefresh(String token) {
        return Long.valueOf(
                extractClaimsRefresh(token).getSubject()
        );
    }

    @Override
    public UUID extractSessionIdAccess(String token) {
        return extractClaimsAccess(token)
                .get("session", UUID.class);
    }

    @Override
    public UUID extractSessionIdRefresh(String token) {
        return extractClaimsRefresh(token)
                .get("session", UUID.class);
    }

    @Override
    public Role extractRole(String token) {
        return extractClaimsAccess(token)
                .get("role", Role.class);
    }

    @Override
    public UUID extractJTI(String token) {
        return extractClaimsAccess(token)
                .get("jti", UUID.class);
    }

    private Claims extractClaimsAccess(String token) {
        return Jwts.parser()
                .verifyWith(accessKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Claims extractClaimsRefresh(String token) {
        return Jwts.parser()
                .verifyWith(refreshKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private void getAccessKey() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getAccessSecret());
        accessKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private void getRefreshKey() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getRefreshSecret());
        refreshKey = Keys.hmacShaKeyFor(keyBytes);
    }
}
