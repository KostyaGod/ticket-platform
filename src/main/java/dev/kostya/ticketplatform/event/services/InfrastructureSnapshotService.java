package dev.kostya.ticketplatform.event.services;

import dev.kostya.ticketplatform.event.entity.CitySnapshot;
import dev.kostya.ticketplatform.event.entity.HallSnapshot;
import dev.kostya.ticketplatform.event.entity.VenueSnapshot;
import dev.kostya.ticketplatform.event.sync.dto.CitySnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.HallSnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.VenueSnapshotDto;

import java.util.List;
import java.util.UUID;

public interface InfrastructureSnapshotService {
    void saveAllCitiesByDto(List<CitySnapshotDto> cities);

    void saveAllVenuesByDto(List<VenueSnapshotDto> venues);

    void saveAllHallsByDto(List<HallSnapshotDto> halls);

    void saveAllCities(List<CitySnapshot> cities);

    void saveAllVenues(List<VenueSnapshot> venues);

    void saveAllHalls(List<HallSnapshot> halls);

    void createCitySnapshot(Long cityId, String cityName);

    void createVenueSnapshot(Long venueId, String venueName, Long cityId);

    void createHallSnapshot(Long hallId, String hallName, Long venueId);

    void updateCitySnapshot(Long cityId, String cityName);

    void updateVenueSnapshot(Long venueId, String venueName, Long cityId);

    void updateHallSnapshot(Long hallId, String hallName, Long venueId);

    void deleteCitySnapshots(Long cityId, UUID deleteOperationId);

    void deleteVenueCitySnapshots(Long venueId, UUID deleteOperationId);

    void deleteHallSnapshots(Long hallId, UUID deleteOperationId);
}
