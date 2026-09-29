package dev.kostya.ticketplatform.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest (
        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 100,
                message = "Password must contain from 8 to 100 characters.")
        String currentPassword,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 100,
                message = "Password must contain from 8 to 100 characters.")
        String newPassword
) { }
