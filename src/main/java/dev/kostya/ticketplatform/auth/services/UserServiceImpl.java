package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.dto.request.ChangePasswordRequest;
import dev.kostya.ticketplatform.auth.dto.request.CreateUserRequest;
import dev.kostya.ticketplatform.auth.dto.request.UpdateProfileRequest;
import dev.kostya.ticketplatform.auth.dto.request.UpdateUserRequest;
import dev.kostya.ticketplatform.auth.dto.response.UserPageResponse;
import dev.kostya.ticketplatform.auth.dto.response.UserResponse;
import dev.kostya.ticketplatform.auth.entity.User;
import dev.kostya.ticketplatform.auth.enums.UserStatus;
import dev.kostya.ticketplatform.auth.exception.*;
import dev.kostya.ticketplatform.auth.mapper.UserMapper;
import dev.kostya.ticketplatform.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = repository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id));

        validateUniqueness(user, request.username(), request.email());

        mapper.updateEntityFromDto(request, user);
        return mapper.toResponse(user);
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        User user = mapper.toEntity(request);
        user.setPasswordHash(
                passwordEncoder.encode(
                        request.password()
                )
        );

        repository.save(user);
        return mapper.toResponse(user);
    }

    @Transactional
    @Override
    public void updateById(Long id, UpdateUserRequest request) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        validateUniqueness(user, request.username(), request.email());

        mapper.updateEntityFromDto(request, user);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setStatus(UserStatus.DELETED);

        long timestamp = System.currentTimeMillis();
        user.setUsername("deleted@" + timestamp + "_" + user.getUsername());
        user.setEmail("deleted." + timestamp + "." + user.getEmail());
    }

    @Transactional
    @Override
    public void changePasswordById(Long id, ChangePasswordRequest request) {
        if (Objects.equals(
                request.currentPassword(),
                request.newPassword()
        )) throw new NewPasswordMatchesOldException();

        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPasswordHash()
        )) throw new CurrentPasswordMismatchException();

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    }

    @Override
    public UserPageResponse getUsers(Pageable pageable) {
        Page<User> userPage = repository.findAll(pageable);

        List<UserResponse> users = userPage.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return new UserPageResponse(
                users,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.hasNext()
        );
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
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
