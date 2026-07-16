package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.security.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CookieServiceImpl implements CookieService {
    private final JwtProperties properties;

    @Override
    public void addAccessCookie(HttpServletResponse response, String token) {
        addCookie(
                response,
                properties.getCookie().getAccessName(),
                token,
                properties.getCookie().getAccessPath(),
                properties.getAccessExpiration()
        );
    }

    @Override
    public void addRefreshCookie(HttpServletResponse response, String token) {
        addCookie(
                response,
                properties.getCookie().getRefreshName(),
                token,
                properties.getCookie().getRefreshPath(),
                properties.getRefreshExpiration()
        );
    }

    @Override
    public void clearCookies(HttpServletResponse response) {
        addCookie(
                response,
                properties.getCookie().getAccessName(),
                "",
                properties.getCookie().getAccessPath(),
                0
        );

        addCookie(
                response,
                properties.getCookie().getRefreshName(),
                "",
                properties.getCookie().getRefreshPath(),
                0
        );
    }

    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return extractCookie(
                request,
                properties.getCookie().getRefreshName()
        );
    }

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return extractCookie(
                request,
                properties.getCookie().getAccessName()
        );
    }

    private Optional<String> extractCookie(
            HttpServletRequest request,
            String cookieName
    ) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }

    private void addCookie(
            HttpServletResponse response,
            String name,
            String value,
            String path,
            long maxAge
    ) {
        JwtProperties.Cookie cookieProperties = properties.getCookie();
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(cookieProperties.isHttpOnly())
                .secure(cookieProperties.isSecure())
                .path(path)
                .sameSite(cookieProperties.getSameSite())
                .maxAge(Duration.ofMillis(maxAge))
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }
}
