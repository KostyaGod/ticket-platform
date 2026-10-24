package dev.kostya.ticketplatform.infrastructure.controllers;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.SeatDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Seat;
import dev.kostya.ticketplatform.infrastructure.services.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EntityReference<Seat>> getSeatReferences(
            @RequestParam(
                    name = "filter",
                    required = false,
                    defaultValue = ""
            ) String nameFilter,
            @PageableDefault(
                    page = 0,
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
    public PageModel<SeatDetailsResponse> getSeats(
            @ModelAttribute SeatFilter filter,
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
    public SeatDetailsResponse getSeat(
            @PathVariable Long id
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public SeatDetailsResponse createSeat(
            @Valid @RequestBody SeatCreateRequest request
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SeatDetailsResponse updateSeat(
            @PathVariable Long id,
            @Valid @RequestBody SeatUpdateRequest request
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeat(
            @PathVariable Long id
    ) {
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public SeatDetailsResponse restoreSeat(
            @PathVariable Long id
    ) {
        return null;
    }
}
