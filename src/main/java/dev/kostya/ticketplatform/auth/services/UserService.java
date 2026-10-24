package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.dto.request.*;
import dev.kostya.ticketplatform.auth.dto.response.UserPageResponse;
import dev.kostya.ticketplatform.auth.dto.response.UserResponse;
import dev.kostya.ticketplatform.auth.entity.User;
import dev.kostya.ticketplatform.auth.enums.UserStatus;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getProfile(Long id);
    UserResponse updateProfile(Long id, UpdateProfileRequest request);
    UserResponse create(CreateUserRequest request);
    UserResponse updateById(Long id, UpdateUserRequest request);
    void deleteById(Long id);
    void changePasswordById(Long id, ChangePasswordRequest request);
    void changeStatusById(Long id, UserStatus status);

    UserPageResponse getUsers(UserFilter filter, Pageable pageable);
    User findByUsername(String username);
    User findByEmail(String email);
}
