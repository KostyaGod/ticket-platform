package dev.kostya.ticketplatform.auth.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface CookieService {
    void addAccessCookie(HttpServletResponse response, String token);
    void addRefreshCookie(HttpServletResponse response, String token);
    void clearCookies(HttpServletResponse response);
    Optional<String> extractRefreshToken(HttpServletRequest request);
    Optional<String> extractAccessToken(HttpServletRequest request);
}
