package com.happyplants.service;

import com.happyplants.dto.ChangePasswordRequest;
import com.happyplants.exception.EmailAlreadyExistsException;
import com.happyplants.exception.InvalidLoginCredentialsException;
import com.happyplants.exception.WeakPasswordException;
import com.happyplants.model.User;
import com.happyplants.dto.LoginRequest;
import com.happyplants.dto.RegisterRequest;
import com.happyplants.repository.UserRepository;
import com.happyplants.util.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, PasswordValidator passwordValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
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

        List<String> passwordErrors = passwordValidator.validate(request.password());
        if (!passwordErrors.isEmpty()) {
            throw new WeakPasswordException(passwordErrors);
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

    public void changePassword(User user, ChangePasswordRequest request) {

        if(!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidLoginCredentialsException();
        }

        List<String> passwordErrors = passwordValidator.validate(request.newPassword());
        if (!passwordErrors.isEmpty()) {
            throw new WeakPasswordException(passwordErrors);
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
