package dev.kostya.ticketplatform.auth.controllers;

import dev.kostya.ticketplatform.auth.dto.response.SessionResponse;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import dev.kostya.ticketplatform.auth.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public List<SessionResponse> getAllMySessions(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return sessionService.getSessions(
                principal.getId(),
                principal.getSessionId()
        );
    }

    @DeleteMapping("/me/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMySession(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID sessionId
    ) {
        sessionService.deleteSession(
                sessionId,
                principal.getId()
        );
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllMySessions(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        sessionService.deleteAllSessions(principal.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/admin/{userId}/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(
            @PathVariable Long userId,
            @PathVariable UUID sessionId
    ) {
        sessionService.deleteSession(
                sessionId,
                userId
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/admin/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllSession(
            @PathVariable Long userId
    ) {
        sessionService.deleteAllSessions(userId);
    }
}
