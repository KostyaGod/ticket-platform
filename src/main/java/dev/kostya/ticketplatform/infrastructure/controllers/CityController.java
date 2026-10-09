package dev.kostya.ticketplatform.infrastructure.controllers;

import dev.kostya.ticketplatform.infrastructure.dto.request.CityCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.PageModel;
import dev.kostya.ticketplatform.infrastructure.services.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageModel<CityResponse> getCities(
            @RequestParam(required = false, defaultValue = "") String name,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return cityService.getCities(name, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CityResponse createCity(
            @Valid CityCreateRequest request
    ) {
        return cityService.createCity(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(
            @PathVariable Long id
    ) {
        cityService.deleteCity(id);
    }

    
}
