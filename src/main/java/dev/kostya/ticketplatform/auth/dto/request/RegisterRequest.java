package dev.kostya.ticketplatform.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required.")
        @Size(min = 3, max = 80,
                message = "Username must contain from 3 to 80 characters.")
        String username,

        @Email(message = "Email is required.")
        @NotBlank(message = "Invalid email address.")
        String email,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 100,
                message = "Password must contain from 8 to 100 characters.")
        String password
) {}
