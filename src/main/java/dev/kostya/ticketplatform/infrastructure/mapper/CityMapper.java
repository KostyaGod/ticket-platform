package dev.kostya.ticketplatform.infrastructure.mapper;

import dev.kostya.ticketplatform.infrastructure.dto.request.CityCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import dev.kostya.ticketplatform.infrastructure.sync.dto.CitySyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.projection.CitySyncProjection;
import org.mapstruct.*;

@Mapper(config = CentralMapperConfig.class)
public interface CityMapper {
    City toEntity(CityCreateRequest request);

    CityDetailsResponse toDetailsResponse(City city);

    @Mapping(target = "children", source = "venues")
    CityWithChildrenResponse toWithChildrenResponse(City city);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CityUpdateRequest request, @MappingTarget City entity);

    CitySyncDto toCitySyncDto(CitySyncProjection city);
}
