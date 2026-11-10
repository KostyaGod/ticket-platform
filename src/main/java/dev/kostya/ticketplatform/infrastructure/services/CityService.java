package dev.kostya.ticketplatform.infrastructure.services;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface CityService {
    PageModel<EntityReference<City>> getReferences(
            String nameFilter,
            Pageable pageable
    );

    CityWithChildrenResponse getCity(Long cityId);

    PageModel<CityDetailsResponse> getCities(
            CityFilter filter,
            Pageable pageable
    );

    CityDetailsResponse create(CityCreateRequest request);

    @Transactional
    CityDetailsResponse update(Long id, CityUpdateRequest request);

    @Transactional
    void delete(Long id, boolean cascade);

    @Transactional
    CityDetailsResponse restore(Long id, boolean cascade);
}
