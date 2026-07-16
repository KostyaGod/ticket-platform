package dev.kostya.ticketplatform.auth.services;

import dev.kostya.ticketplatform.auth.entity.User;

public interface UserService {
    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
