package dev.kostya.ticketplatform.infrastructure.controllers;

import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.VenueDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.VenueWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Venue;
import dev.kostya.ticketplatform.infrastructure.services.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/venues")
@RequiredArgsConstructor
public class VenueController {
    private final VenueService venueService;

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EntityReference<Venue>> getVenuesReferences(
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
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageModel<VenueDetailsResponse> getVenues(
            @ModelAttribute VenueFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VenueWithChildrenResponse getVenue(
            @PathVariable Long id
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public VenueDetailsResponse createVenue(
            @Valid @RequestBody VenueCreateRequest request
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VenueDetailsResponse updateVenue(
            @PathVariable Long id,
            @Valid @RequestBody VenueUpdateRequest request
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenue(
            @PathVariable Long id
    ) {
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public VenueDetailsResponse restoreVenue(
            @PathVariable Long id
    ) {
        return null;
    }
}
