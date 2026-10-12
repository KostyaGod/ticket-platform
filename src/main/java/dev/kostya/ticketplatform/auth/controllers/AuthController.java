package dev.kostya.ticketplatform.auth.controllers;

import dev.kostya.ticketplatform.auth.dto.request.LoginRequest;
import dev.kostya.ticketplatform.auth.dto.request.RegisterRequest;
import dev.kostya.ticketplatform.auth.exception.InvalidCredentialsException;
import dev.kostya.ticketplatform.auth.services.CookieService;
import dev.kostya.ticketplatform.auth.security.Tokens;
import dev.kostya.ticketplatform.auth.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        Tokens tokens = authService.register(request);
        addCookies(response, tokens);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public void login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        Tokens tokens = authService.login(request);
        addCookies(response, tokens);
    }

    @GetMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public void refresh(
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        String refreshToken = cookieService.extractRefreshToken(request).orElseThrow(InvalidCredentialsException::new);
        Tokens tokens = authService.refresh(refreshToken);
        addCookies(response, tokens);
    }

    @GetMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        String refreshToken = cookieService.extractRefreshToken(request).orElseThrow(InvalidCredentialsException::new);
        authService.logout(refreshToken);
        cookieService.clearCookies(response);
    }

    private void addCookies(HttpServletResponse response, Tokens tokens) {
        cookieService.addAccessCookie(response, tokens.access());
        cookieService.addRefreshCookie(response, tokens.refresh());
    }
}
