package com.happyplants.service;

import com.happyplants.dto.requests.ChangePasswordRequest;
import com.happyplants.exception.InvalidLoginCredentialsException;
import com.happyplants.exception.UserNotFoundException;
import com.happyplants.exception.WeakPasswordException;
import com.happyplants.model.User;
import com.happyplants.repository.UserRepository;
import com.happyplants.util.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private Authentication auth;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setPasswordHash("oldHashedPassword");
    }

    @Nested
    class findUserByIdTests {

        @Test
        void shouldReturnCurrentUser() {
            UUID id = UUID.randomUUID();
            User user = new User();
            user.setId(id);

            when(auth.getName()).thenReturn(id.toString());
            when(userRepository.findById(id)).thenReturn(Optional.of(user));

            User result = userService.getCurrentUser(auth);

            assertNotNull(result);
            assertEquals(id, result.getId());
            verify(userRepository).findById(id);
        }

        @Test
        void shouldThrowExceptionUserNotFound() {
            UUID id = UUID.randomUUID();

            when(auth.getName()).thenReturn(id.toString());
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userService.getCurrentUser(auth));

            verify(userRepository).findById(id);
        }
    }

    @Nested
    class ChangePasswordTests {

        @Test
        @DisplayName("TC-USER-03.1: Ensures that changePassword() updates the password and saves the user with the new password.")
        void shouldUpdatePasswordAndSaveUser_changePassword() {
            ChangePasswordRequest passwordRequest = new ChangePasswordRequest("CurrentPassword123!", "NewPassword123!");

            lenient().when(passwordEncoder.matches("CurrentPassword123!", "oldHashedPassword")).thenReturn(true);
            lenient().when(passwordValidator.validate("NewPassword123!")).thenReturn(List.of());
            lenient().when(passwordEncoder.encode("NewPassword123!")).thenReturn("newHashedPassword");

            userService.changePassword(user, passwordRequest);

            assertEquals("newHashedPassword", user.getPasswordHash());

            verify(passwordEncoder).matches("CurrentPassword123!", "oldHashedPassword");
            verify(passwordEncoder).encode("NewPassword123!");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("TC-USER-03.1: Verifies that InvalidLoginCredentialsException is thrown if the user enters the wrong current password while attempting to change their password.")
        void shouldThrowInvalidLoginCredentialsException_changePassword() {
            ChangePasswordRequest passwordRequest = new ChangePasswordRequest("WrongPassword", "NewPassword");
            when(passwordEncoder.matches("WrongPassword", "oldHashedPassword")).thenReturn(false);

            assertThrows(InvalidLoginCredentialsException.class, () -> userService.changePassword(user, passwordRequest));

            verify(userRepository, never()).save(any());
            verify(passwordValidator, never()).validate(anyString());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("TC-USER-03.1: Verifies that WeakPasswordException is thrown if the user enters a weak new password.")
        void shouldThrowWeakPasswordException_changePassword() {
            ChangePasswordRequest passwordRequest = new ChangePasswordRequest("WrongPassword", "NewPassword");

            lenient().when(passwordEncoder.matches("WrongPassword", "oldHashedPassword")).thenReturn(true);
            lenient().when(passwordValidator.validate("NewPassword")).thenReturn(List.of());
            lenient().when(passwordEncoder.encode("NewPassword")).thenReturn("newHashedPassword");

            assertThrows(WeakPasswordException.class, () -> userService.changePassword(user, passwordRequest));

            verify(userRepository, never()).save(any());
            verify(passwordValidator, never()).validate(anyString());
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Nested
    class DeleteUserTests {
        
        @Test
        @DisplayName("TC-USER-04: Verifies that a user account can be deleted.")
        void shouldDeleteUser() {
            User userToDelete = new User();
            userToDelete.setId(UUID.randomUUID());

            userService.deleteUser(userToDelete);

            verify(userRepository).delete(userToDelete);
        }
    }
}
