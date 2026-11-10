package dev.kostya.ticketplatform.infrastructure.services;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.response.SeatDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Seat;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface SeatService {

    PageModel<EntityReference<Seat>> getReferences(
            Integer rowNumber,
            Integer seatNumber,
            Pageable pageable
    );

    SeatDetailsResponse getSeat(Long seatId);

    PageModel<SeatDetailsResponse> getSeats(SeatFilter filter, Pageable pageable);

    SeatDetailsResponse create(SeatCreateRequest request);

    @Transactional
    SeatDetailsResponse update(Long id, SeatUpdateRequest request);

    void delete(Long id, boolean cascade);

    SeatDetailsResponse restore(Long id, boolean cascade);
}
