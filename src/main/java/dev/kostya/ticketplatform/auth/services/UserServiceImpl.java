package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.entity.User;
import dev.kostya.ticketplatform.auth.mapper.UserMapper;
import dev.kostya.ticketplatform.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findUserByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    @Override
    public User findUserByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }
}
