package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.dto.request.LoginRequest;
import dev.kostya.ticketplatform.auth.dto.request.RegisterRequest;
import dev.kostya.ticketplatform.auth.security.Tokens;

public interface AuthService {
    Tokens login(LoginRequest request);
    Tokens register(RegisterRequest register);
    Tokens refresh(String refreshToken);
    void logout(String refreshToken);
}
