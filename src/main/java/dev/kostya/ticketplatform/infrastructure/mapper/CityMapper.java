package dev.kostya.ticketplatform.infrastructure.mapper;

import dev.kostya.ticketplatform.infrastructure.dto.request.CityCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = CentralMapperConfig.class)
public interface CityMapper extends CentralMapperConfig {
    City toEntity(CityCreateRequest request);

    CityDetailsResponse toDetailsResponse(City city);

    List<CityDetailsResponse> toDetailsResponseList(List<City> cityList);

    @Mapping(target = "children", source = "venues")
    CityWithChildrenResponse toWithChildrenResponse(City city);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CityUpdateRequest request, @MappingTarget City entity);
}
