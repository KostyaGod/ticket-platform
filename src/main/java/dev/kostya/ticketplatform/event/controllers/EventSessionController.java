package dev.kostya.ticketplatform.event.controllers;

import dev.kostya.ticketplatform.event.dto.base.Range;
import dev.kostya.ticketplatform.event.dto.base.RequestMeta;
import dev.kostya.ticketplatform.event.dto.request.SessionCalendarFilter;
import dev.kostya.ticketplatform.event.dto.request.SessionCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.SessionFilter;
import dev.kostya.ticketplatform.event.dto.request.SessionUpdateRequest;
import dev.kostya.ticketplatform.event.dto.base.PageModel;
import dev.kostya.ticketplatform.event.dto.response.SessionDetailsResponse;
import dev.kostya.ticketplatform.event.dto.response.SessionShortResponse;
import dev.kostya.ticketplatform.event.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.event.services.EventSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/event/sessions")
@RequiredArgsConstructor
public class EventSessionController {
    private final EventSessionService sessionService;

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<SessionShortResponse> getSessions(
            @ModelAttribute RequestMeta meta,
            @ModelAttribute Range<Instant> dateFilter,
            @PathVariable Long eventId,
            @PageableDefault(
                    size = 20,
                    sort = "startAt",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return sessionService.getShortSessions( dateFilter, eventId, pageable );
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @GetMapping("/by-event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<SessionShortResponse> getSessionsByEvent(
            @ModelAttribute RequestMeta meta,
            @PathVariable Long eventId,
            @RequestParam(
                    name = "filter",
                    required = false,
                    defaultValue = "ACTIVE_ONLY"
            ) DeletedViewFilter deletedFilter,
            @PageableDefault(
                    size = 20,
                    sort = "startAt",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return sessionService.getSessionsByEvent( eventId, deletedFilter, pageable );
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @GetMapping("/data")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<SessionDetailsResponse> getSessionsTable(
            @ModelAttribute RequestMeta meta,
            @ModelAttribute SessionFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "startAt",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return sessionService.getDetailedSessions( filter, pageable );
    }

    @GetMapping("/available-days/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<LocalDate> getSessionDates(
            @ModelAttribute RequestMeta meta,
            @ModelAttribute SessionCalendarFilter calendarFilter,
            @PathVariable Long eventId
    ) {
        return sessionService.getSessionDates( calendarFilter.date(), calendarFilter.time(), eventId );
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public SessionDetailsResponse createSession(
            @RequestBody SessionCreateRequest request
    ) {
        return sessionService.create( request );
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SessionUpdateRequest updateSession(
            @RequestBody SessionUpdateRequest request,
            @PathVariable Long id
    ) {
        return sessionService.update( id, request );
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(
            @ModelAttribute RequestMeta meta,
            @PathVariable Long id,
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        sessionService.delete( id, cascade );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public SessionDetailsResponse restoreSession(
            @PathVariable Long id,
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        return sessionService.restore( id, cascade );
    }
}
