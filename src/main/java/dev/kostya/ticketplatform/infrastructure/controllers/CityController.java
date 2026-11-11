package dev.kostya.ticketplatform.infrastructure.controllers;

import dev.kostya.ticketplatform.common.dto.response.ErrorResponse;
import dev.kostya.ticketplatform.common.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityDetailsResponse;
import dev.kostya.ticketplatform.common.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import dev.kostya.ticketplatform.infrastructure.services.CityService;
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

@Tag(name = "Города", description = "Управление городами присутствия площадок и кинотеатров")
@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @Operation(
            summary = "Получение списка городов (краткий справочник)",
            description = "Возвращает легковесный список городов (ID и наименование) для выпадающих списков с поддержкой фильтрации по названию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список городов успешно получен")
    })
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EntityReference<City>> getCityReferences(
            @Parameter(description = "Фильтр по названию города (поиск подстроки)")
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
        return cityService.getReferences( nameFilter, pageable );
    }

    @Operation(
            summary = "Получение детального списка городов с фильтрацией",
            description = "Возвращает пагинированный список городов с метаданными и расширенными фильтрами"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Страница городов получена"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется ADMIN или SERVER)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageModel<CityDetailsResponse> getCities(
            @ModelAttribute CityFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return cityService.getCities( filter, pageable );
    }

    @Operation(
            summary = "Получение информации о городе с дочерними площадками",
            description = "Возвращает данные города и список расположенных в нем площадок/кинотеатров"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Город найден",
                    content = @Content(schema = @Schema(implementation = CityWithChildrenResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Город с указанным ID не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CityWithChildrenResponse getCity(
            @Parameter(description = "ID города", required = true)
            @PathVariable Long id
    ) {
        return cityService.getCity( id );
    }

    @Operation(
            summary = "Создание нового города",
            description = "Добавляет новый город в справочник инфраструктуры"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Город успешно создан",
                    content = @Content(schema = @Schema(implementation = CityDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CityDetailsResponse createCity(
            @Valid @RequestBody CityCreateRequest request
    ) {
         return cityService.create( request );
    }

    @Operation(
            summary = "Обновление параметров города",
            description = "Изменяет название или другие атрибуты существующего города"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Город успешно обновлен",
                    content = @Content(schema = @Schema(implementation = CityDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Город не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CityDetailsResponse updateCity(
            @Parameter(description = "ID города", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CityUpdateRequest request
    ) {
        return cityService.update( id, request );
    }

    @Operation(
            summary = "Мягкое удаление города",
            description = "Помечает город как удаленный (soft delete) с возможностью каскадного удаления дочерних площадок"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Город удален"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Город не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(
            @Parameter(description = "ID города", required = true)
            @PathVariable Long id,
            @Parameter(description = "Флаг каскадного удаления связанных площадок")
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        cityService.delete( id, cascade );
    }

    @Operation(
            summary = "Восстановление ранее удаленного города",
            description = "Восстанавливает город из мягкого удаления с опциональным каскадным восстановлением дочерних объектов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Город восстановлен",
                    content = @Content(schema = @Schema(implementation = CityDetailsResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Город не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public CityDetailsResponse restoreCity(
            @Parameter(description = "ID города", required = true)
            @PathVariable Long id,
            @Parameter(description = "Флаг каскадного восстановления связанных сущностей")
            @RequestParam(
                    required = false,
                    defaultValue = "true"
            )
            boolean cascade
    ) {
        return cityService.restore( id, cascade );
    }
}
