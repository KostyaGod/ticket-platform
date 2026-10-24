package dev.kostya.ticketplatform.infrastructure.controllers;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.HallDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.entity.Hall;
import dev.kostya.ticketplatform.infrastructure.dto.response.HallWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.services.HallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/halls")
@RequiredArgsConstructor
public class HallController {
    private final HallService hallService;

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EntityReference<Hall>> getHallReferences(
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
    public PageModel<HallDetailsResponse> getHalls(
            @ModelAttribute HallFilter filter,
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
    public HallWithChildrenResponse getHall(
            @PathVariable Long id
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public HallDetailsResponse createHall(
            @Valid @RequestBody HallCreateRequest request
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public HallDetailsResponse updateHall(
            @PathVariable Long id,
            @Valid @RequestBody HallUpdateRequest request
    ) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHall(
            @PathVariable Long id
    ) {
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public HallDetailsResponse restoreHall(
            @PathVariable Long id
    ) {
        return null;
    }
}
