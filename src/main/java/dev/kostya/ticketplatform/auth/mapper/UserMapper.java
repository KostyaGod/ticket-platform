package dev.kostya.ticketplatform.auth.mapper;

import dev.kostya.ticketplatform.auth.dto.request.RegisterRequest;
import dev.kostya.ticketplatform.auth.dto.request.UpdateProfileRequest;
import dev.kostya.ticketplatform.auth.dto.request.UpdateUserRequest;
import dev.kostya.ticketplatform.auth.dto.response.UserResponse;
import dev.kostya.ticketplatform.auth.entity.User;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role",  ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(RegisterRequest dto);

    @Mapping(target = "sessionId", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    UserPrincipal toPrincipal(User user);

    UserResponse toResponse(User entity);

    @InheritInverseConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateProfileRequest dto, @MappingTarget User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateUserRequest dto, @MappingTarget User entity);
}
