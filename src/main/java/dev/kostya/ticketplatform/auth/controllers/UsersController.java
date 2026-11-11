package dev.kostya.ticketplatform.auth.controllers;

import dev.kostya.ticketplatform.auth.dto.request.*;
import dev.kostya.ticketplatform.auth.dto.response.UserPageResponse;
import dev.kostya.ticketplatform.auth.dto.response.UserResponse;
import dev.kostya.ticketplatform.auth.enums.UserStatus;
import dev.kostya.ticketplatform.auth.exception.AdminSelfModificationException;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import dev.kostya.ticketplatform.auth.services.SessionService;
import dev.kostya.ticketplatform.auth.services.UserService;
import dev.kostya.ticketplatform.common.dto.response.ErrorResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Пользователи", description = "Управление профилем пользователя, смена пароля и администрирование учетных записей")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;
    private final SessionService sessionService;

    @Operation(
            summary = "Получение профиля текущего пользователя",
            description = "Возвращает информацию о текущем авторизованном пользователе"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные профиля получены",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return userService.getProfile(principal.getId());
    }

    @Operation(
            summary = "Обновление данных своего профиля",
            description = "Позволяет пользователю обновить персональные данные (имя, контакты и т.д.)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль успешно обновлен",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateProfile(principal.getId(), request);
    }

    @Operation(
            summary = "Удаление собственного аккаунта",
            description = "Мягкое удаление (soft delete) своего профиля и прекращение всех активных сессий в Redis"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Аккаунт успешно удален"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.deleteById(principal.getId());
        sessionService.deleteAllSessions(principal.getId());
    }

    @Operation(
            summary = "Административное удаление пользователя",
            description = "Позволяет администратору пометить пользователя как удаленного и завершить все его сессии"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id
    ) {
        userService.deleteById(id);
        sessionService.deleteAllSessions(id);
    }

    @Operation(
            summary = "Восстановление удаленного пользователя",
            description = "Восстанавливает учетную запись пользователя из мягкого удаления"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно восстановлен",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse restoreUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id,
            @Parameter(description = "Флаг каскадного восстановления связанных сущностей")
            @RequestParam(required = false, defaultValue = "true")
            boolean cascade
    ) {
        return userService.restore(id, cascade);
    }

    @Operation(
            summary = "Смена пароля текущего пользователя",
            description = "Обновляет пароль пользователя после проверки совпадения текущего пароля"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пароль успешно изменен"),
            @ApiResponse(responseCode = "400", description = "Новый пароль совпадает со старым или текущий пароль неверен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePasswordById(principal.getId(), request);
    }

    @Operation(
            summary = "Поиск и получение списка пользователей",
            description = "Возвращает страницу со списком пользователей с возможностью фильтрации по роли, статусу, дате создания"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Страница пользователей успешно получена",
                    content = @Content(schema = @Schema(implementation = UserPageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется ADMIN или SERVER)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public UserPageResponse getUsers(
            @ModelAttribute UserFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return userService.getUsers(filter, pageable);
    }

    @Operation(
            summary = "Административное создание пользователя",
            description = "Создает учетную запись с указанием роли и параметров доступа"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибки валидации входных данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email или username уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return userService.create(request);
    }

    @Operation(
            summary = "Административное обновление данных пользователя",
            description = "Обновляет параметры пользователя. Администратор не может модифицировать сам себя через этот метод"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибки валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Попытка модификации собственного аккаунта администратором или недостаток прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "ID обновляемого пользователя", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        if (principal.getId().equals(id)) {
            throw new AdminSelfModificationException();
        }

        return userService.updateById(id, request);
    }

    @Operation(
            summary = "Изменение статуса пользователя",
            description = "Позволяет заблокировать или активировать учетную запись пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Статус успешно изменен"),
            @ApiResponse(responseCode = "403", description = "Попытка изменить статус самому себе или недостаток прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setUserStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id,
            @Parameter(description = "Новый статус учетной записи (ACTIVE, BLOCKED и т.д.)", required = true)
            @RequestParam UserStatus status
    ) {
        if (principal.getId().equals(id)) {
            throw new AdminSelfModificationException();
        }

        userService.changeStatusById(id, status);
    }
}
