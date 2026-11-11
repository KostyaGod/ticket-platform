package dev.kostya.ticketplatform.event.controllers;

import dev.kostya.ticketplatform.common.dto.base.Range;
import dev.kostya.ticketplatform.common.dto.response.ErrorResponse;
import dev.kostya.ticketplatform.event.dto.base.RequestMeta;
import dev.kostya.ticketplatform.event.dto.request.SessionCalendarFilter;
import dev.kostya.ticketplatform.event.dto.request.SessionCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.SessionFilter;
import dev.kostya.ticketplatform.event.dto.request.SessionUpdateRequest;
import dev.kostya.ticketplatform.common.dto.base.PageModel;
import dev.kostya.ticketplatform.event.dto.response.SessionDetailsResponse;
import dev.kostya.ticketplatform.event.dto.response.SessionShortResponse;
import dev.kostya.ticketplatform.common.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.event.services.EventSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Tag(name = "Сеансы мероприятий", description = "Управление сеансами показов, расписанием и календарем доступных дат")
@RestController
@RequestMapping("/event/sessions")
@RequiredArgsConstructor
public class EventSessionController {
    private final EventSessionService sessionService;

    @Operation(
            summary = "Публичное расписание сеансов мероприятия",
            description = "Возвращает краткий список сеансов для конкретного мероприятия с возможностью фильтрации по временному диапазону"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сеансы успешно получены")
    })
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<SessionShortResponse> getSessions(
            @ModelAttribute RequestMeta meta,
            @ModelAttribute Range<Instant> dateFilter,
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable Long eventId,
            @PageableDefault(
                    size = 20,
                    sort = "startAt",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return sessionService.getShortSessions( dateFilter, eventId, pageable );
    }

    @Operation(
            summary = "Административный список сеансов мероприятия",
            description = "Возвращает сеансы мероприятия с возможностью просмотра удаленных записей"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сеансы получены"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @GetMapping("/by-event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<SessionShortResponse> getSessionsByEvent(
            @ModelAttribute RequestMeta meta,
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable Long eventId,
            @Parameter(description = "Фильтр состояния (активные/удаленные/все)")
            @RequestParam(
                    name = "filter",
                    required = false,
                    defaultValue = "ACTIVE_ONLY"
            ) DeletedViewFilter deletedFilter,
            @PageableDefault(
                    size = 20,
                    sort = "startAt",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return sessionService.getSessionsByEvent( eventId, deletedFilter, pageable );
    }

    @Operation(
            summary = "Таблица сеансов с детальной фильтрацией",
            description = "Полнотекстовая таблица сеансов с фильтрацией по залам, ценам, датам и статусу"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Таблица сеансов получена"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @GetMapping("/data")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<SessionDetailsResponse> getSessionsTable(
            @ModelAttribute RequestMeta meta,
            @ModelAttribute SessionFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "startAt",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return sessionService.getDetailedSessions( filter, pageable );
    }

    @Operation(
            summary = "Календарь доступных дней с сеансами",
            description = "Возвращает список уникальных дат, в которые запланированы показы для указанного мероприятия с учетом часового пояса"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Даты успешно получены",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LocalDate.class))))
    })
    @GetMapping("/available-days/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<LocalDate> getSessionDates(
            @ModelAttribute RequestMeta meta,
            @ModelAttribute SessionCalendarFilter calendarFilter,
            @Parameter(description = "Часовой пояс клиента (например, 'Europe/Moscow')", required = true)
            @RequestParam ZoneId timezone,
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable Long eventId
    ) {
        if (timezone == null) {
            timezone = ZoneOffset.UTC;
        }

        return sessionService.getSessionDates(
                eventId,
                calendarFilter.date(),
                calendarFilter.time(),
                timezone
        );
    }

    @Operation(
            summary = "Создание нового сеанса",
            description = "Планирует новый сеанс для мероприятия в выбранном зале и устанавливает базовую стоимость"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Сеанс успешно создан",
                    content = @Content(schema = @Schema(implementation = SessionDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public SessionDetailsResponse createSession(
            @RequestBody SessionCreateRequest request
    ) {
        return sessionService.create( request );
    }

    @Operation(
            summary = "Обновление параметров сеанса",
            description = "Изменяет время начала, зал или статус существующего сеанса"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сеанс обновлен",
                    content = @Content(schema = @Schema(implementation = SessionDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Сеанс не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SessionDetailsResponse updateSession(
            @RequestBody SessionUpdateRequest request,
            @Parameter(description = "ID сеанса", required = true)
            @PathVariable Long id
    ) {
        return sessionService.update( id, request );
    }

    @Operation(
            summary = "Мягкое удаление сеанса",
            description = "Помечает сеанс как отмененный/удаленный (soft delete)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Сеанс удален"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Сеанс не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'SERVER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(
            @ModelAttribute RequestMeta meta,
            @Parameter(description = "ID сеанса", required = true)
            @PathVariable Long id,
            @Parameter(description = "Флаг каскадного удаления")
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        sessionService.delete( id, cascade );
    }

    @Operation(
            summary = "Восстановление сеанса",
            description = "Восстанавливает ранее удаленный сеанс"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сеанс восстановлен",
                    content = @Content(schema = @Schema(implementation = SessionDetailsResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Сеанс не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public SessionDetailsResponse restoreSession(
            @Parameter(description = "ID сеанса", required = true)
            @PathVariable Long id,
            @Parameter(description = "Флаг каскадного восстановления")
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        return sessionService.restore( id, cascade );
    }
}
