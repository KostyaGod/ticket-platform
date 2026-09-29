package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.dto.request.ChangePasswordRequest;
import dev.kostya.ticketplatform.auth.dto.request.CreateUserRequest;
import dev.kostya.ticketplatform.auth.dto.request.UpdateProfileRequest;
import dev.kostya.ticketplatform.auth.dto.request.UpdateUserRequest;
import dev.kostya.ticketplatform.auth.dto.response.UserPageResponse;
import dev.kostya.ticketplatform.auth.dto.response.UserResponse;
import dev.kostya.ticketplatform.auth.entity.User;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getProfile(Long id);
    UserResponse updateProfile(Long id, UpdateProfileRequest request);
    UserResponse create(CreateUserRequest request);
    void updateById(Long id, UpdateUserRequest request);
    void deleteById(Long id);
    void changePasswordById(Long id, ChangePasswordRequest request);

    UserPageResponse getUsers(Pageable pageable);
    User findByUsername(String username);
    User findByEmail(String email);
}
