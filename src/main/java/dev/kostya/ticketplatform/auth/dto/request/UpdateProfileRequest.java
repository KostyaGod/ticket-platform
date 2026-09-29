package dev.kostya.ticketplatform.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 3, max = 80,
                message = "Username must contain from 3 to 80 characters.")
        @Pattern(regexp = "^[a-zA-Z0-9_.-]+$",
                message = "Username can contain only letters, numbers, '.', '_' and '-'")
        String username,

        @Email(message = "Invalid email address.")
        String email
) { }
