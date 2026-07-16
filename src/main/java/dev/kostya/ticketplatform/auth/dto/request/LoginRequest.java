package dev.kostya.ticketplatform.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Email(message = "Email is required.")
        @NotBlank(message = "Invalid email address.")
        String email,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 255,
                message = "Password must contain from 8 to 100 characters.")
        String password
) { }
