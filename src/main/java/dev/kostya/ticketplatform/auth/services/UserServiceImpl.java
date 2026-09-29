package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.dto.request.UpdateProfileRequest;
import dev.kostya.ticketplatform.auth.dto.request.UpdateUserRequest;
import dev.kostya.ticketplatform.auth.dto.response.UserResponse;
import dev.kostya.ticketplatform.auth.entity.User;
import dev.kostya.ticketplatform.auth.enums.UserStatus;
import dev.kostya.ticketplatform.auth.exception.EmailAlreadyExistsException;
import dev.kostya.ticketplatform.auth.exception.InvalidCredentialsException;
import dev.kostya.ticketplatform.auth.exception.UserNotFoundException;
import dev.kostya.ticketplatform.auth.exception.UsernameAlreadyExistsException;
import dev.kostya.ticketplatform.auth.mapper.UserMapper;
import dev.kostya.ticketplatform.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getProfile(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return mapper.toResponse(user);
    }

    @Transactional
    @Override
    public void updateProfile(Long id, UpdateProfileRequest request) {
        User user = repository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id));

        validateUniqueness(user, request.username(), request.email());

        mapper.updateEntityFromDto(request, user);
    }

    @Transactional
    @Override
    public void updateUser(Long id, UpdateUserRequest request) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        validateUniqueness(user, request.username(), request.email());

        mapper.updateEntityFromDto(request, user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setStatus(UserStatus.DELETED);

        long timestamp = System.currentTimeMillis();
        user.setUsername("deleted@" + timestamp + "_" + user.getUsername());
        user.setEmail("deleted." + timestamp + "." + user.getEmail());
    }

    @Override
    public User findUserByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    @Override
    public User findUserByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    private void validateUniqueness(User currentUser, String newUsername, String newEmail) {
        if (newUsername != null && !newUsername.equals(currentUser.getUsername())) {
            if (repository.existsByUsername(newUsername)) {
                throw new UsernameAlreadyExistsException();
            }
        }
        if (newEmail != null && !newEmail.equals(currentUser.getEmail())) {
            if (repository.existsByEmail(newEmail)) {
                throw new EmailAlreadyExistsException();
            }
        }
    }
}
