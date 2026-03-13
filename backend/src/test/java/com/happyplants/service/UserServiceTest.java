package com.happyplants.service;

import com.happyplants.exception.UserNotFoundException;
import com.happyplants.model.User;
import com.happyplants.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication auth;

    @InjectMocks
    private UserService userService;

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
