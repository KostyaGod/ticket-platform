package dev.kostya.ticketplatform.infrastructure.controllers;

import dev.kostya.ticketplatform.common.dto.response.ErrorResponse;
import dev.kostya.ticketplatform.common.dto.base.EntityReference;
import dev.kostya.ticketplatform.common.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.SeatDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Seat;
import dev.kostya.ticketplatform.infrastructure.services.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Места в зале", description = "Управление посадочными местами зрительных залов (ряды, номера, категории)")
@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @Operation(
            summary = "Краткий список мест (справочник)",
            description = "Возвращает легковесный список мест с фильтрацией по номеру ряда и номеру места"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список мест успешно получен")
    })
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EntityReference<Seat>> getSeatReferences(
            @Parameter(description = "Номер ряда")
            @RequestParam(
                    name = "rowNumber",
                    required = false
            ) Integer rowNumber,
            @Parameter(description = "Номер места в ряду")
            @RequestParam(
                    name = "seatNumber",
                    required = false
            ) Integer seatNumber,
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return seatService.getReferences(rowNumber, seatNumber, pageable);
    }

    @Operation(
            summary = "Детальный список мест с фильтрами",
            description = "Возвращает пагинированный список мест с фильтрацией по залу, категориям цен и статусу"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список мест получен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageModel<SeatDetailsResponse> getSeats(
            @ModelAttribute SeatFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return seatService.getSeats(filter, pageable);
    }

    @Operation(
            summary = "Детальная информация о месте",
            description = "Возвращает параметры конкретного посадочного места по его идентификатору"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Место найдено",
                    content = @Content(schema = @Schema(implementation = SeatDetailsResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Место не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SeatDetailsResponse getSeat(
            @Parameter(description = "ID места", required = true)
            @PathVariable Long id
    ) {
        return seatService.getSeat(id);
    }

    @Operation(
            summary = "Создание посадочного места",
            description = "Добавляет новое место в указанный зал с указанием ряда и категории"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Место создано",
                    content = @Content(schema = @Schema(implementation = SeatDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public SeatDetailsResponse createSeat(
            @Valid @RequestBody SeatCreateRequest request
    ) {
        return seatService.create(request);
    }

    @Operation(
            summary = "Обновление параметров места",
            description = "Изменяет номер ряда, места или тип существующего посадочного места"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Место обновлено",
                    content = @Content(schema = @Schema(implementation = SeatDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Место не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SeatDetailsResponse updateSeat(
            @Parameter(description = "ID места", required = true)
            @PathVariable Long id,
            @Valid @RequestBody SeatUpdateRequest request
    ) {
        return seatService.update(id, request);
    }

    @Operation(
            summary = "Мягкое удаление места",
            description = "Помечает посадочное место как удаленное (soft delete)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Место удалено"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Место не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeat(
            @Parameter(description = "ID места", required = true)
            @PathVariable Long id,
            @Parameter(description = "Флаг каскадного удаления")
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        seatService.delete(id, cascade);
    }

    @Operation(
            summary = "Восстановление места",
            description = "Восстанавливает удаленное ранее посадочное место"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Место успешно восстановлено",
                    content = @Content(schema = @Schema(implementation = SeatDetailsResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Место не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public SeatDetailsResponse restoreSeat(
            @Parameter(description = "ID места", required = true)
            @PathVariable Long id,
            @Parameter(description = "Флаг каскадного восстановления")
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        return seatService.restore(id, cascade);
    }
}
