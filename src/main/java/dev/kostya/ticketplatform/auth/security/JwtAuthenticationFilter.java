package dev.kostya.ticketplatform.auth.security;

import dev.kostya.ticketplatform.auth.services.CookieService;
import dev.kostya.ticketplatform.auth.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CookieService cookieService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> tokenOpt = cookieService.extractAccessToken(request);

        if (tokenOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = tokenOpt.get();

        if (!jwtService.validateAccess(token)) {
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
}
