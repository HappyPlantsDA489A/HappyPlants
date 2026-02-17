package com.happyplants.service;

import com.happyplants.exception.UserNotFoundException;
import com.happyplants.model.User;
import com.happyplants.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(Authentication auth) {
        UUID id = UUID.fromString(auth.getName());
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }
}
