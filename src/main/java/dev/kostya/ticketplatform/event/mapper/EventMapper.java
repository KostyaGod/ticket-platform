package dev.kostya.ticketplatform.event.mapper;

import dev.kostya.ticketplatform.event.dto.base.EntityReference;
import dev.kostya.ticketplatform.event.dto.request.EventCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.EventUpdateRequest;
import dev.kostya.ticketplatform.event.dto.response.EventDetailsResponse;
import dev.kostya.ticketplatform.event.entity.Event;
import dev.kostya.ticketplatform.event.entity.IdentifiableAndNamed;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CentralMapperConfig.class)
public interface EventMapper {
    Event toEntity(EventCreateRequest request);

    EventDetailsResponse toDetailsResponse(Event event);

    EntityReference<Event> toReference(IdentifiableAndNamed dto);

    Event toEntity(IdentifiableAndNamed dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EventUpdateRequest request, @MappingTarget Event entity);
}
