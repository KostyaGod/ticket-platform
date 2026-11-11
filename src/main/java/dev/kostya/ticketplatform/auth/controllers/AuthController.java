package dev.kostya.ticketplatform.auth.controllers;

import dev.kostya.ticketplatform.auth.dto.request.LoginRequest;
import dev.kostya.ticketplatform.auth.dto.request.RegisterRequest;
import dev.kostya.ticketplatform.auth.exception.InvalidCredentialsException;
import dev.kostya.ticketplatform.auth.services.CookieService;
import dev.kostya.ticketplatform.auth.security.Tokens;
import dev.kostya.ticketplatform.auth.services.AuthService;
import dev.kostya.ticketplatform.common.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Аутентификация", description = "Эндпоинты для регистрации, входа, обновления токенов и выхода из системы")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieService cookieService;

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает учетную запись пользователя и устанавливает JWT токены в HttpOnly cookies"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email или username уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        Tokens tokens = authService.register(request);
        addCookies(response, tokens);
    }

    @Operation(
            summary = "Вход в систему",
            description = "Аутентифицирует пользователя по логину (username или email) и паролю, создавая новую сессию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация, токены установлены в cookies"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации тела запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Учетная запись заблокирована",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public void login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        Tokens tokens = authService.login(request);
        addCookies(response, tokens);
    }

    @Operation(
            summary = "Обновление пары токенов (Refresh Flow)",
            description = "Обновляет access и refresh токены по действующему refresh токену из HttpOnly cookie с ротацией JTI сессии"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токены успешно обновлены"),
            @ApiResponse(responseCode = "401", description = "Refresh токен не передан, недействителен или сессия отозвана",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public void refresh(
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        String refreshToken = cookieService.extractRefreshToken(request).orElseThrow(InvalidCredentialsException::new);
        Tokens tokens = authService.refresh(refreshToken);
        addCookies(response, tokens);
    }

    @Operation(
            summary = "Выход из системы",
            description = "Завершает текущую сессию пользователя в Redis и очищает установленные cookies"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешный выход из системы"),
            @ApiResponse(responseCode = "401", description = "Refresh токен отсутствует или недействителен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        String refreshToken = cookieService.extractRefreshToken(request).orElseThrow(InvalidCredentialsException::new);
        authService.logout(refreshToken);
        cookieService.clearCookies(response);
    }

    private void addCookies(HttpServletResponse response, Tokens tokens) {
        cookieService.addAccessCookie(response, tokens.access());
        cookieService.addRefreshCookie(response, tokens.refresh());
    }
}
