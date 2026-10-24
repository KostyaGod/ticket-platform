package dev.kostya.ticketplatform.auth.dto.request;

import dev.kostya.ticketplatform.auth.enums.Role;
import dev.kostya.ticketplatform.auth.enums.UserStatus;
import jakarta.validation.constraints.*;

public record CreateUserRequest (
        @NotBlank(message = "Username is required.")
        @Size(min = 3, max = 80,
                message = "Username must contain from 3 to 80 characters.")
        @Pattern(regexp = "^[a-zA-Z0-9_.-]+$",
                message = "Username can contain only letters, numbers, '.', '_' and '-'")
        String username,

        @NotBlank(message = "Email is required.")
        @Email(message = "Invalid email address.")
        String email,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 100,
                message = "Password must contain from 8 to 100 characters.")
        String password,

        @NotNull(message = "Role is required.")
        Role role,

        @NotNull(message = "Status is required.")
        UserStatus userStatus
) { }
