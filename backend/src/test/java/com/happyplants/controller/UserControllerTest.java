package com.happyplants.controller;

import com.happyplants.dto.ChangePasswordRequest;
import com.happyplants.exception.InvalidLoginCredentialsException;
import com.happyplants.exception.WeakPasswordException;
import com.happyplants.model.User;
import com.happyplants.repository.UserRepository;
import com.happyplants.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setPasswordHash("encoded_old_password");
    }

    @Test
    @DisplayName("TC-USER-03.1: Should change password successfully when current password is correct and new password is strong")
    void changePassword_Success() {

    }
}