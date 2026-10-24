package dev.kostya.ticketplatform.auth.mapper;

import dev.kostya.ticketplatform.auth.dto.request.CreateUserRequest;
import dev.kostya.ticketplatform.auth.dto.request.RegisterRequest;
import dev.kostya.ticketplatform.auth.dto.request.UpdateProfileRequest;
import dev.kostya.ticketplatform.auth.dto.request.UpdateUserRequest;
import dev.kostya.ticketplatform.auth.dto.response.UserResponse;
import dev.kostya.ticketplatform.auth.entity.User;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import org.mapstruct.*;

@Mapper(config = CentralMapperConfig.class)
public interface UserMapper {
    User toEntity(RegisterRequest dto);

    User toEntity(CreateUserRequest dto);

    UserPrincipal toPrincipal(User user);

    UserResponse toResponse(User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateProfileRequest dto, @MappingTarget User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateUserRequest dto, @MappingTarget User entity);
}
