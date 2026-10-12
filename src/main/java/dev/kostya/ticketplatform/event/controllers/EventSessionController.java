package dev.kostya.ticketplatform.event.controllers;

import dev.kostya.ticketplatform.event.dto.base.DateRange;
import dev.kostya.ticketplatform.event.dto.request.RequestMeta;
import dev.kostya.ticketplatform.event.dto.request.SessionCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.SessionFilter;
import dev.kostya.ticketplatform.event.dto.base.TimeRange;
import dev.kostya.ticketplatform.event.dto.request.SessionUpdateRequest;
import dev.kostya.ticketplatform.event.dto.response.PageModel;
import dev.kostya.ticketplatform.event.dto.response.SessionResponse;
import dev.kostya.ticketplatform.event.dto.response.SessionShortResponse;
import dev.kostya.ticketplatform.event.services.EventService;
import dev.kostya.ticketplatform.event.services.EventSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/event/sessions")
@RequiredArgsConstructor
public class EventSessionController {
    private final EventSessionService sessionService;
    private final EventService eventService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageModel<SessionShortResponse> getSessions(
            @ModelAttribute RequestMeta meta,
            @ModelAttribute DateRange dateFilter,
            @PageableDefault(
                    size = 20,
                    sort = "startAt",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return null;
    }

    @GetMapping("/available-days")
    @ResponseStatus(HttpStatus.OK)
    public List<LocalDate> getSessionDates(
            @ModelAttribute RequestMeta meta,
            @ModelAttribute DateRange dateFilter,
            @ModelAttribute TimeRange timeFilter
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @GetMapping("/data")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<SessionResponse> getSessionsData(
            @ModelAttribute RequestMeta meta,
            @ModelAttribute SessionFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "startAt",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponse createSession(
            @RequestBody SessionCreateRequest request
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(
            @ModelAttribute RequestMeta meta,
            @PathVariable Long id
    ) {
        return;
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SessionUpdateRequest updateSession(
            @RequestBody SessionUpdateRequest request,
            @PathVariable String id
    ) {
        return null;
    }
}
