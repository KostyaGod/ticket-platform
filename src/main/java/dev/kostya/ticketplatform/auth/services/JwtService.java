package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.enums.Role;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;

import java.util.UUID;

public interface JwtService {
    String generateAccessToken(UserPrincipal user, UUID sessionId);
    String generateRefreshToken(UserPrincipal user, UUID sessionId, UUID jti);
    boolean validateAccess(String token);
    boolean validateRefresh(String token);
    Long extractUserIdAccess(String token);
    Long extractUserIdRefresh(String token);
    UUID extractSessionIdAccess(String token);
    UUID extractSessionIdRefresh(String token);
    Role extractRole(String token);
    UUID extractJTI(String token);
}
