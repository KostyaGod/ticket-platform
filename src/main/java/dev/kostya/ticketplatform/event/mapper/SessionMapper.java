package dev.kostya.ticketplatform.event.mapper;

import dev.kostya.ticketplatform.event.dto.request.SessionCreateRequest;
import dev.kostya.ticketplatform.event.dto.response.SessionDetailsResponse;
import dev.kostya.ticketplatform.event.dto.response.SessionShortResponse;
import dev.kostya.ticketplatform.event.entity.EventSession;
import org.mapstruct.*;

@Mapper(config = CentralMapperConfig.class)
public interface SessionMapper {
    @Mapping(target = "event.id", source = "eventId")
    @Mapping(target = "hall.id", source = "hallId")
    @Mapping(target = "salesStartAt", source = "salesTimeRange.from")
    @Mapping(target = "salesEndAt", source = "salesTimeRange.to")
    @Mapping(target = "startAt", source = "sessionTimeRange.from")
    @Mapping(target = "endAt", source = "sessionTimeRange.to")
    EventSession toEntity(SessionCreateRequest request);

    SessionDetailsResponse toResponse(EventSession session);

    SessionShortResponse toShortResponse(EventSession session);

    @Mapping(target = "event.id", source = "eventId")
    @Mapping(target = "salesStartAt", source = "salesTimeRange.from")
    @Mapping(target = "salesEndAt", source = "salesTimeRange.to")
    @Mapping(target = "startAt", source = "sessionTimeRange.from")
    @Mapping(target = "endAt", source = "sessionTimeRange.to")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(SessionCreateRequest request, @MappingTarget EventSession entity);
}
