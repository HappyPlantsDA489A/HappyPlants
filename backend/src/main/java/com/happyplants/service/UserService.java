package com.happyplants.service;

import com.happyplants.dto.ChangePasswordRequest;
import com.happyplants.exception.InvalidLoginCredentialsException;
import com.happyplants.exception.UserNotFoundException;
import com.happyplants.exception.WeakPasswordException;
import com.happyplants.model.User;
import com.happyplants.repository.UserRepository;
import com.happyplants.util.PasswordValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordValidator = new PasswordValidator();
        this.passwordEncoder = passwordEncoder;

    }

    public User getCurrentUser(Authentication auth) {
        UUID id = UUID.fromString(auth.getName());
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
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
