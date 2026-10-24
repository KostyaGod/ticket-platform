package dev.kostya.ticketplatform.infrastructure.sync.dto;

import dev.kostya.ticketplatform.infrastructure.entity.City;

public record CitySyncDto(
        Long id,
        String name
) {
    public CitySyncDto(City city) {
        this(
                city.getId(),
                city.getName()
        );
    }
}
