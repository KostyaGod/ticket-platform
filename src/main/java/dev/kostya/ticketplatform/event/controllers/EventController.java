package dev.kostya.ticketplatform.event.controllers;

import dev.kostya.ticketplatform.common.dto.base.EntityReference;
import dev.kostya.ticketplatform.common.dto.response.ErrorResponse;
import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.request.EventCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.EventFilter;
import dev.kostya.ticketplatform.event.dto.request.EventUpdateRequest;
import dev.kostya.ticketplatform.event.dto.response.EventCatalogResponse;
import dev.kostya.ticketplatform.event.dto.response.EventDetailsResponse;
import dev.kostya.ticketplatform.common.dto.base.PageModel;
import dev.kostya.ticketplatform.event.entity.Event;
import dev.kostya.ticketplatform.common.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.event.services.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Мероприятия (Фильмы)", description = "Управление каталогом мероприятий, афишей и фильмами")
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    public final EventService eventService;

    @Operation(
            summary = "Каталог мероприятий для афиши",
            description = "Возвращает витрину активных мероприятий в указанном городе с фильтрацией по жанрам, датам и рейтингу"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Каталог успешно получен")
    })
    @GetMapping("/catalog")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EventCatalogResponse> getEventCatalog(
            @Parameter(description = "ID города, для которого запрашивается афиша", required = true)
            @RequestParam Long cityId,
            @ModelAttribute EventCatalogFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return eventService.getCatalog( cityId, filter, pageable );
    }

    @Operation(
            summary = "Краткий список мероприятий (справочник)",
            description = "Возвращает легковесный список мероприятий (ID и заголовок) для селектов и фильтров"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список мероприятий получен")
    })
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EntityReference<Event>> getEventReferences(
            @Parameter(description = "Фильтр по названию мероприятия")
            @RequestParam(
                    name = "filter",
                    required = false,
                    defaultValue = ""
            ) String nameFilter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return eventService.getReferences( nameFilter, pageable );
    }

    @Operation(
            summary = "Публичная карточка мероприятия",
            description = "Возвращает детальную информацию о мероприятии (описание, длительность, постеры, возрастные ограничения)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мероприятие найдено",
                    content = @Content(schema = @Schema(implementation = EventDetailsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Мероприятие не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDetailsResponse getEvent(
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable Long id
    ) {
        return eventService.getDetails( id );
    }

    @Operation(
            summary = "Административная карточка мероприятия",
            description = "Возвращает детальную информацию о мероприятии с учетом статуса мягкого удаления"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мероприятие найдено",
                    content = @Content(schema = @Schema(implementation = EventDetailsResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Мероприятие не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDetailsResponse getEvent(
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable Long id,
            @Parameter(description = "Режим отображения удаленных сущностей")
            @RequestParam(
                    name = "filter",
                    required = false,
                    defaultValue = "ACTIVE_ONLY"
            ) DeletedViewFilter deletedFilter
    ) {
        return eventService.getDetails( id, deletedFilter );
    }

    @Operation(
            summary = "Административный поиск и фильтрация мероприятий",
            description = "Возвращает расширенный список мероприятий для бэк-офиса с фильтрами по статусам и метаданным"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список мероприятий получен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EventDetailsResponse> getEvents(
            @ModelAttribute EventFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return eventService.getEvents( filter, pageable );
    }

    @Operation(
            summary = "Создание нового мероприятия",
            description = "Добавляет фильм или событие в каталог"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Мероприятие создано",
                    content = @Content(schema = @Schema(implementation = EventDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventDetailsResponse createEvent(
            @RequestBody EventCreateRequest eventRequest
    ) {
        return eventService.create( eventRequest );
    }

    @Operation(
            summary = "Редактирование мероприятия",
            description = "Обновляет параметры существующего мероприятия"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мероприятие успешно обновлено",
                    content = @Content(schema = @Schema(implementation = EventDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Мероприятие не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDetailsResponse updateEvent(
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable Long id,
            @RequestBody EventUpdateRequest request
    ) {
        return eventService.update( id, request );
    }

    @Operation(
            summary = "Мягкое удаление мероприятия",
            description = "Помечает мероприятие как удаленное (soft delete) с опциональным каскадным удалением сеансов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Мероприятие удалено"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Мероприятие не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable Long id,
            @Parameter(description = "Флаг каскадного удаления связанных сеансов")
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        eventService.delete( id, cascade );
    }

    @Operation(
            summary = "Восстановление мероприятия",
            description = "Восстанавливает мероприятие из корзины soft delete"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мероприятие восстановлено",
                    content = @Content(schema = @Schema(implementation = EventDetailsResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Мероприятие не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public EventDetailsResponse restoreEvent(
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable Long id,
            @Parameter(description = "Флаг каскадного восстановления связанных сеансов")
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        return eventService.restore( id, cascade );
    }
}
