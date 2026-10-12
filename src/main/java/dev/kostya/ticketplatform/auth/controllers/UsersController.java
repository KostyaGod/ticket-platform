package dev.kostya.ticketplatform.auth.controllers;

import dev.kostya.ticketplatform.auth.dto.request.*;
import dev.kostya.ticketplatform.auth.dto.response.UserPageResponse;
import dev.kostya.ticketplatform.auth.dto.response.UserResponse;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import dev.kostya.ticketplatform.auth.services.SessionService;
import dev.kostya.ticketplatform.auth.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;
    private final SessionService sessionService;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return userService.getProfile(principal.getId());
    }

    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateProfile(principal.getId(), request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.deleteById(principal.getId());
        sessionService.deleteAllSessions(principal.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(
            @PathVariable Long id
    ) {
        userService.deleteById(id);
        sessionService.deleteAllSessions(id);
    }

    /// In progress
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse restoreUser(
            @PathVariable Long id
    ) {
        return null;
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePasswordById(principal.getId(), request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public UserPageResponse getUsers(
            @ModelAttribute UserFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return userService.getUsers(filter, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return userService.create(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
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
        userService.updateById(id, request);

        return ResponseEntity.ok().build();
    }
}
