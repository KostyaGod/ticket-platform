package dev.kostya.ticketplatform.event.controllers;

import dev.kostya.ticketplatform.event.dto.base.EntityReference;
import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.request.EventCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.EventFilter;
import dev.kostya.ticketplatform.event.dto.request.EventUpdateRequest;
import dev.kostya.ticketplatform.event.dto.response.EventCatalogResponse;
import dev.kostya.ticketplatform.event.dto.response.EventDetailsResponse;
import dev.kostya.ticketplatform.event.dto.base.PageModel;
import dev.kostya.ticketplatform.event.entity.Event;
import dev.kostya.ticketplatform.event.enums.DeletedViewFilter;
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
        return eventService.getCatalog( cityId, filter, pageable );
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EntityReference<Event>> getEventReferences(
            @RequestParam(
                    name = "filter",
                    required = false,
                    defaultValue = ""
            ) String nameFilter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return eventService.getReferences( nameFilter, pageable );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDetailsResponse getEvent(
            @PathVariable Long id
    ) {
        return eventService.getDetails( id );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDetailsResponse getEvent(
            @PathVariable Long id,
            @RequestParam(
                    name = "filter",
                    required = false,
                    defaultValue = "ACTIVE_ONLY"
            ) DeletedViewFilter deletedFilter
    ) {
        return eventService.getDetails( id, deletedFilter );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EventDetailsResponse> getEvents(
            @ModelAttribute EventFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return eventService.getEvents( filter, pageable );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventDetailsResponse createEvent(
            @RequestBody EventCreateRequest eventRequest
    ) {
        return eventService.create( eventRequest );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDetailsResponse updateEvent(
            @PathVariable Long id,
            @RequestBody EventUpdateRequest request
    ) {
        return eventService.update( id, request );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(
            @PathVariable Long id,
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        eventService.delete( id, cascade );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public EventDetailsResponse restoreEvent(
            @PathVariable Long id,
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        return eventService.restore( id, cascade );
    }
}
