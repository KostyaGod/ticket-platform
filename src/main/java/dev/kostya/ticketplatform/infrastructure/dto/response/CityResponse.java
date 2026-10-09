package dev.kostya.ticketplatform.infrastructure.dto.response;

import dev.kostya.ticketplatform.infrastructure.entity.City;

public record CityResponse(
        Long id,
        String name
) {
    public CityResponse(City city) {
        this(city.getId(), city.getName());
    }
}
