package dev.kostya.ticketplatform.infrastructure.services;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import org.springframework.data.domain.Pageable;

public interface CityService {
    PageModel<EntityReference<City>> getCityRefList(String name, Pageable pageable);

    EntityReference<City> createCity(CityCreateRequest request);

    void deleteCity(Long id);
}
