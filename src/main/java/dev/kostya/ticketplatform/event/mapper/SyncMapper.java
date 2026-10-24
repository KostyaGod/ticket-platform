package dev.kostya.ticketplatform.event.mapper;

import dev.kostya.ticketplatform.event.entity.CitySnapshot;
import dev.kostya.ticketplatform.event.entity.HallSnapshot;
import dev.kostya.ticketplatform.event.entity.VenueSnapshot;
import dev.kostya.ticketplatform.event.sync.dto.CitySnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.HallSnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.VenueSnapshotDto;
import jakarta.persistence.EntityManager;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = CentralMapperConfig.class)
public interface SyncMapper {
    CitySnapshotDto toDto(CitySnapshot city);

    VenueSnapshotDto toDto(VenueSnapshot venue);

    HallSnapshotDto toDto(HallSnapshot hall);

    CitySnapshot fromDto(CitySnapshotDto dto);

    @Mapping(target = "city", source = "cityId", qualifiedByName = "referenceToCity")
    VenueSnapshot fromDto(VenueSnapshotDto dto, @Context EntityManager entityManager);

    @Mapping(target = "venue", source = "venueId", qualifiedByName = "referenceToVenue")
    HallSnapshot fromDto(HallSnapshotDto dto, @Context EntityManager entityManager);

    @Named("referenceToVenue")
    default VenueSnapshot referenceToVenue(
            Long venueId,
            @Context EntityManager entityManager
    ) {
        if (venueId == null) {
            return null;
        }
        return entityManager.getReference(VenueSnapshot.class, venueId);
    }

    @Named("referenceToCity")
    default CitySnapshot referenceToCity(
            Long cityId,
            @Context EntityManager entityManager
    ) {
        if (cityId == null) {
            return null;
        }
        return entityManager.getReference(CitySnapshot.class, cityId);
    }
}
