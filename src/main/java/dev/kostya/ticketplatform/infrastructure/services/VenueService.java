package dev.kostya.ticketplatform.infrastructure.services;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.response.VenueDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.VenueWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Venue;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface VenueService {
    PageModel<EntityReference<Venue>> getReferences(String nameFilter, Pageable pageable);

    VenueWithChildrenResponse getVenue(Long venueId);

    PageModel<VenueDetailsResponse> getVenues(VenueFilter filter, Pageable pageable);

    VenueDetailsResponse create(VenueCreateRequest request);

    @Transactional
    VenueDetailsResponse update(Long id, VenueUpdateRequest request);

    @Transactional
    void delete(Long id, boolean cascade);

    @Transactional
    VenueDetailsResponse restore(Long id, boolean cascade);
}
