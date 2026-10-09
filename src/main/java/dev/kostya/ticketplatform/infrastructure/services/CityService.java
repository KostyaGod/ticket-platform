package dev.kostya.ticketplatform.infrastructure.services;

import dev.kostya.ticketplatform.infrastructure.dto.request.CityCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.PageModel;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public interface CityService {
    PageModel<CityResponse> getCities(String name, Pageable pageable);

    CityResponse createCity(@Valid CityCreateRequest request);

    void deleteCity(Long id);
}
