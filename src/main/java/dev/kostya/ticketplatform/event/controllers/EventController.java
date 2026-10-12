package dev.kostya.ticketplatform.event.controllers;

import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.request.EventCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.EventFilter;
import dev.kostya.ticketplatform.event.dto.request.EventUpdateRequest;
import dev.kostya.ticketplatform.event.dto.response.EventCatalogResponse;
import dev.kostya.ticketplatform.event.dto.response.EventResponse;
import dev.kostya.ticketplatform.event.dto.response.PageModel;
import dev.kostya.ticketplatform.event.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    public final EventService eventService;

    @GetMapping("/catalog")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EventCatalogResponse> getEventCatalog(
            @RequestParam Long cityId,
            @ModelAttribute EventCatalogFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return eventService.getEventsCatalog(cityId, filter, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse getEventDetails(
            @PathVariable Long id
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EventResponse> getEvents(
            @RequestParam Long cityId,
            @RequestParam Long organizationId,
            @ModelAttribute EventFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(
            @RequestBody EventCreateRequest eventRequest
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse updateEvent(
            @PathVariable Long id,
            @RequestBody EventUpdateRequest request
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(
            @PathVariable Long id
    ) {
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse restoreEvent(
            @PathVariable Long id
    ) {
        return null;
    }
}
