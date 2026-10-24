package dev.kostya.ticketplatform.infrastructure.mapper;

import dev.kostya.ticketplatform.infrastructure.dto.request.SeatCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.SeatDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Seat;
import dev.kostya.ticketplatform.infrastructure.sync.dto.SeatSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.projection.SeatSyncProjection;
import org.mapstruct.*;

@Mapper(config = CentralMapperConfig.class)
public interface SeatMapper {
    @Mapping(target = "hall.id", source = "hallId")
    Seat toEntity(SeatCreateRequest seat);

    SeatDetailsResponse toDetailsResponse(Seat seat);

    @Mapping(target = "hall.id", source = "hallId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(SeatUpdateRequest request, @MappingTarget Seat entity);

    SeatSyncDto toSeatSyncDto(SeatSyncProjection seat);
}
