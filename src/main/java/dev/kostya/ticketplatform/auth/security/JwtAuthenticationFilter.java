package dev.kostya.ticketplatform.auth.security;

import dev.kostya.ticketplatform.auth.services.CookieService;
import dev.kostya.ticketplatform.auth.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Фильтр безопасности сервлетов, перехватывающий каждый HTTP-запрос ровно один раз
 * для выполнения аутентификации на основе JWT токенов доступа.
 * <p>
 * Принцип работы:
 * <ol>
 *   <li>Извлекает токен доступа с поддержкой двух каналов передачи (в приоритетном порядке):
 *       <ul>
 *         <li>HTTP-заголовок {@code Authorization: Bearer <token>} (для API-клиентов и микросервисов).</li>
 *         <li>Безопасная HttpOnly Cookie {@code accessToken} (для SPA / браузерных сессий).</li>
 *       </ul>
 *   </li>
 *   <li>Валидирует цифровую подпись и срок действия токена с помощью {@link JwtService}.</li>
 *   <li>При успешной валидации воссоздает легковесный объект {@link UserPrincipal} непосредственно из claim'ов токена
 *       (ID пользователя, роль, ID сессии), не выполняя блокирующих запросов к реляционной БД (Stateless подход).</li>
 *   <li>Помещает объект аутентификации в {@link SecurityContextHolder} для дальнейших проверок прав доступа Spring Security.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtService jwtService;
    private final CookieService cookieService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractToken(request);

        if (token == null || !jwtService.validateAccess(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        UserPrincipal userPrincipal = new UserPrincipal(
                jwtService.extractUserIdAccess(token),
                jwtService.extractRole(token),
                jwtService.extractSessionIdAccess(token)
        );

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null,
                    userPrincipal.getAuthorities()
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает JWT токен из заголовка Authorization либо из cookie.
     *
     * @param request текущий HTTP-запрос
     * @return строка JWT токена либо {@code null}, если токен не найден
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length()).trim();
        }
        return cookieService.extractAccessToken(request).orElse(null);
    }
}
