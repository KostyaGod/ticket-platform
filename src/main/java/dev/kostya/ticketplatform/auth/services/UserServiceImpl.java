package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.dto.request.*;
import dev.kostya.ticketplatform.auth.dto.response.PageResponse;
import dev.kostya.ticketplatform.auth.dto.response.UserPageResponse;
import dev.kostya.ticketplatform.auth.dto.response.UserResponse;
import dev.kostya.ticketplatform.auth.entity.User;
import dev.kostya.ticketplatform.auth.enums.UserStatus;
import dev.kostya.ticketplatform.auth.exception.*;
import dev.kostya.ticketplatform.auth.mapper.UserMapper;
import dev.kostya.ticketplatform.auth.repositories.UserRepository;
import dev.kostya.ticketplatform.auth.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getProfile(Long id) {
        User user = repository.findById( id )
                .orElseThrow( () -> new UserNotFoundException(id) );

        return mapper.toResponse( user );
    }

    @Transactional
    @Override
    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = repository.findById( id )
                        .orElseThrow( () -> new UserNotFoundException(id) );

        validateUniqueness( user, request.username(), request.email() );

        mapper.updateEntityFromDto( request, user );
        return mapper.toResponse( user );
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        validateUniqueness( null, request.username(), request.email() );

        User user = mapper.toEntity( request );
        user.setPasswordHash(
                passwordEncoder.encode(
                        request.password()
                )
        );

        repository.save( user );

        return mapper.toResponse( user );
    }

    @Transactional
    @Override
    public UserResponse updateById(Long id, UpdateUserRequest request) {
        User user = repository.findById( id )
                .orElseThrow( () -> new UserNotFoundException( id ) );

        validateUniqueness( user, request.username(), request.email() );

        mapper.updateEntityFromDto( request, user );

        return mapper.toResponse( user );
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        User user = repository.findById( id )
                .orElseThrow( () -> new UserNotFoundException( id ) );

        UUID operationId = UUID.randomUUID();
        user.markDeleted( operationId );

        // Каскадный вызов микросервисов для удаления связанных сущностей
        // В частности Scope Table
    }

    @Transactional
    @Override
    public void changePasswordById(Long id, ChangePasswordRequest request) {
        if (Objects.equals(
                request.currentPassword(),
                request.newPassword()
        )) throw new NewPasswordMatchesOldException();

        User user = repository.findById( id )
                .orElseThrow( () -> new UserNotFoundException( id ) );

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPasswordHash()
        )) throw new CurrentPasswordMismatchException();

        user.setPasswordHash( passwordEncoder.encode( request.newPassword() ) );
    }

    @Transactional
    @Override
    public void changeStatusById(Long id, UserStatus status) {
        User user = repository.findById( id )
                .orElseThrow(() -> new UserNotFoundException( id ));

        user.setUserStatus( status );
    }

    @Override
    public UserPageResponse getUsers(UserFilter filter, Pageable pageable) {
        Specification<User> spec = UserSpecification.build( filter );
        Page<User> userPage = repository.findAll( spec, pageable );

        List<UserResponse> users = userPage.getContent().stream()
                .map( mapper::toResponse )
                .toList();

        return new UserPageResponse(
                users,
                new PageResponse(
                        userPage.getNumber(),
                        userPage.getSize(),
                        userPage.getTotalElements(),
                        userPage.getTotalPages(),
                        userPage.hasNext()
                )
        );
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername( username ).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail( email ).orElse(null);
    }

    @Transactional
    @Override
    public UserResponse restore(Long id, boolean cascade) {
        User user = repository.findById( id )
                .orElseThrow( () -> new UserNotFoundException( id ) );

        if ( !user.isDeleted() ) throw new UserNotDeletedException( id );

        if ( repository.existsByEmailAndDeletedAtIsNull( user.getEmail() ) ) {
            throw new RestoreUserException("email");
        }

        if ( repository.existsByUsernameAndDeletedAtIsNull( user.getUsername() ) ) {
            throw new RestoreUserException("username");
        }

        UUID operationId = user.restore();

        if (cascade) {
            // Каскадный вызов микросервисов для восстановления связанных сущностей
            // В частности Scope Table
        }

        return mapper.toResponse( user );
    }

    private void validateUniqueness(User currentUser, String newUsername, String newEmail) {
        if (newUsername != null && (currentUser == null || !newUsername.equals( currentUser.getUsername() ))) {
            if (repository.existsByUsernameAndDeletedAtIsNull( newUsername )) {
                throw new UsernameAlreadyExistsException();
            }
        }
        if (newEmail != null && (currentUser == null || !newEmail.equals( currentUser.getEmail() ))) {
            if (repository.existsByEmailAndDeletedAtIsNull( newEmail )) {
                throw new EmailAlreadyExistsException();
            }
        }
    }
}
