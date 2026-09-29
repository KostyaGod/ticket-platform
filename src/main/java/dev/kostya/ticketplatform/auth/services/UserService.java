package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.dto.request.UpdateProfileRequest;
import dev.kostya.ticketplatform.auth.dto.request.UpdateUserRequest;
import dev.kostya.ticketplatform.auth.dto.response.UserResponse;
import dev.kostya.ticketplatform.auth.entity.User;

public interface UserService {
    UserResponse getProfile(Long id);
    void updateProfile(Long id, UpdateProfileRequest request);
    void updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
