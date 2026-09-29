package dev.kostya.ticketplatform.auth.controllers;

import dev.kostya.ticketplatform.auth.dto.request.UpdateProfileRequest;
import dev.kostya.ticketplatform.auth.dto.request.UpdateUserRequest;
import dev.kostya.ticketplatform.auth.dto.response.UserResponse;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import dev.kostya.ticketplatform.auth.services.SessionService;
import dev.kostya.ticketplatform.auth.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;
    private final SessionService sessionService;

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserResponse userResponse = userService.getProfile(principal.getId());

        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        userService.updateProfile(principal.getId(), request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.deleteUser(principal.getId());
        sessionService.deleteAllSessions(principal.getId());

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        if (principal.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("You cannot update your own profile through " +
                            "the admin management endpoint. Please use " +
                            "the profile settings page.");
        }
        userService.updateUser(id, request);

        return ResponseEntity.ok().build();
    }
}
