package dev.kostya.ticketplatform.infrastructure.mapper;

import dev.kostya.ticketplatform.infrastructure.dto.request.HallCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.HallDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.HallWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Hall;
import dev.kostya.ticketplatform.infrastructure.entity.Seat;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = CentralMapperConfig.class)
public interface HallMapper extends CentralMapperConfig {
    @Mapping(target = "venue.id", source = "venueId")
    Hall toEntity(HallCreateRequest request);

    @AfterMapping
    default void generateSeats(HallCreateRequest request, @MappingTarget Hall hall) {
        if (request.seatMap() == null) return;

        int rows = request.seatMap().rowCount();
        int seatInRow = request.seatMap().seatCount();

        for (int r = 1; r <= rows; r++) {
            for (int s = 1; s <= seatInRow; s++) {
                Seat seat = new Seat();
                seat.setRowNumber(r);
                seat.setSeatNumber(s);
                hall.addSeat(seat);
            }
        }
    }

    HallDetailsResponse toDetailsResponse(Hall hall);

    List<HallDetailsResponse> toDetailsResponseList(List<Hall> hallList);

    @Mapping(target = "children", source = "seats")
    HallWithChildrenResponse toWithChildrenResponse(Hall hall);

    @Mapping(target = "venue.id", source = "venueId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(HallUpdateRequest request, @MappingTarget Hall hall);
}
