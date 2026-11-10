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

import java.util.List;

@Mapper(config = CentralMapperConfig.class)
public interface EventMapper extends CentralMapperConfig {
    Event toEntity(EventCreateRequest request);

    EventDetailsResponse toDetailsResponse(Event event);

    EntityReference<Event> toReference(IdentifiableAndNamed dto);

    List<EntityReference<Event>> toReferenceList(List<IdentifiableAndNamed> dtoList);

    List<EventDetailsResponse> toDetailsResponseList(List<Event> events);

    Event toEntity(IdentifiableAndNamed dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EventUpdateRequest request, @MappingTarget Event entity);
}
