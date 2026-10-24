package dev.kostya.ticketplatform.infrastructure.mapper;

import dev.kostya.ticketplatform.infrastructure.dto.request.VenueCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.VenueDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.VenueWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Venue;
import dev.kostya.ticketplatform.infrastructure.sync.dto.VenueSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.projection.VenueSyncProjection;
import org.mapstruct.*;

@Mapper(config = CentralMapperConfig.class)
public interface VenueMapper {
    @Mapping(target = "city.id", source = "cityId")
    Venue toEntity(VenueCreateRequest request);

    VenueDetailsResponse toDetailsResponse(Venue venue);

    @Mapping(target = "children", source = "halls")
    VenueWithChildrenResponse toWithChildrenResponse(Venue venue);

    @Mapping(target = "city.id", source = "cityId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(VenueUpdateRequest request, @MappingTarget Venue entity);

    VenueSyncDto toVenueSyncDto(VenueSyncProjection venue);
}
