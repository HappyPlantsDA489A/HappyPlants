package com.happyplants.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happyplants.dto.LoginRequest;
import com.happyplants.dto.RegisterRequest;
import com.happyplants.exception.InvalidLoginCredentialsException;
import com.happyplants.model.User;
import com.happyplants.service.AuthService;
import com.happyplants.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController Tests")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        public void shouldReturnCreatedWhenRegistrationIsSuccessful() throws Exception {
            RegisterRequest request = new RegisterRequest("test@example.com", "TestUser", "StrongPass123!");
            User savedUser = new User();
            savedUser.setEmail(request.email());
            savedUser.setDisplayName(request.displayName());

            when(authService.registerUser(any(RegisterRequest.class))).thenReturn(savedUser);

            // Act & Assert
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.displayName").value("TestUser"));
        }

        @Test
        public void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
            RegisterRequest request = new RegisterRequest("invalid-email", "User", "Password123!");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        public void shouldReturnOkAndLoginSuccessMessageWhenCredentialsAreValid() throws Exception {
            LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPass123!");
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setEmail(loginRequest.email());

            when(authService.verifyLogin(any(LoginRequest.class))).thenReturn(user);

            mockMvc.perform(post("/api/auth/log-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Login successful"));
        }

        @Test
        public void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
            LoginRequest loginRequest = new LoginRequest("test@example.com", "WrongPassword123!");

            doThrow(new InvalidLoginCredentialsException())
                    .when(authService).verifyLogin(any(LoginRequest.class));

            mockMvc.perform(post("/api/auth/log-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void shouldReturnBadRequestWhenLoginEmailFormatIsInvalid() throws Exception {
            LoginRequest loginRequest = new LoginRequest("not-an-email", "StrongPass123!");

            mockMvc.perform(post("/api/auth/log-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenLoginFieldsAreBlank() throws Exception {
            LoginRequest loginRequest = new LoginRequest("", "");

            mockMvc.perform(post("/api/auth/log-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        public void shouldReturnOkAndLogoutSuccessMessageWhenLogoutIsSuccessful() throws Exception {
            mockMvc.perform(delete("/api/auth/log-out"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Logout successful"));
        }

        @Test
        public void shouldClearCookieWhenLogoutIsSuccessful() throws Exception {
            mockMvc.perform(delete("/api/auth/log-out"))
                    .andExpect(status().isOk())
                    .andExpect(cookie().maxAge("HAPPY_COOKIE", 0));
        }

        @Test
        public void shouldReturnOkWhenLogoutIsCalledWithActiveSession() throws Exception {
            mockMvc.perform(delete("/api/auth/log-out")
                            .sessionAttr("SPRING_SECURITY_CONTEXT", "mockSessionValue"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Logout successful"));
        }
    }


    @Nested
    @DisplayName("Check Auth Tests")
    class CheckAuthTests {

        @Test
        public void shouldReturnOkAndUsernameWhenUserIsAuthenticated() throws Exception {
            UUID userId = UUID.randomUUID();
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId.toString(), null, Collections.emptyList()
            );

            MockMvc securedMvc = webAppContextSetup(webApplicationContext)
                    .apply(springSecurity())
                    .build();

            securedMvc.perform(get("/api/auth/check-auth")
                            .with(authentication(auth)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Logged in as: " + userId));
        }
    }
}
