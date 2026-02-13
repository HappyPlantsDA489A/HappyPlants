package com.happyplants.service;

import com.happyplants.exception.EmailAlreadyExistsException;
import com.happyplants.exception.InvalidLoginCredentialsException;
import com.happyplants.model.User;
import com.happyplants.model.dto.LoginRequest;
import com.happyplants.model.dto.RegisterRequest;
import com.happyplants.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User registerUser(RegisterRequest request)  {
        if (existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User();
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        return userRepository.save(user);
    }

    public User verifyLogin(LoginRequest request) {
        User user = findByEmail(request.email())
                .orElseThrow(InvalidLoginCredentialsException::new);

        boolean validPassword = passwordEncoder.matches(request.password(), user.getPasswordHash());

        if (validPassword) {
            return user;
        } else {
            throw new InvalidLoginCredentialsException();
        }
    }
}
