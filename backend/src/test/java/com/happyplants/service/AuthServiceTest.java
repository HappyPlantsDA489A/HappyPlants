package com.happyplants.service;

import com.happyplants.exception.WeakPasswordException;
import com.happyplants.model.User;
import com.happyplants.dto.RegisterRequest;
import com.happyplants.repository.UserRepository;
import com.happyplants.util.PasswordValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.happyplants.exception.EmailAlreadyExistsException;
import com.happyplants.exception.InvalidLoginCredentialsException;
import com.happyplants.dto.LoginRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordValidator passwordValidator;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("TC-USER-02, TC-USER-02.3, TC-USER-03.1, TC-USER-05: Ensures that the password saved in the database is encrypted.")
        public void shouldHashPasswordBeforeSaving() {

            RegisterRequest request = new RegisterRequest("secure@test.com", "SafeUser", "ValidPass123!");

            // Låtsas att e-posten är ledig
            when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
            // Låtsas att valideringen godkänner lösenordet (returnerar tom lista)
            when(passwordValidator.validate(request.password())).thenReturn(Collections.emptyList());
            // Låtsas hasha lösenordet
            when(passwordEncoder.encode(request.password())).thenReturn("hashed_och_saltat_loesenord");


            authService.registerUser(request);

            // Fånga objektet som skickas till userRepository.save()
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();

            // Lösenordet i entiteten är INTE klartexten!
            assertNotEquals("ValidPass123!", savedUser.getPasswordHash());
            assertEquals("hashed_och_saltat_loesenord", savedUser.getPasswordHash());
        }

        @Test
        @DisplayName("TC-USER-02.3.1, TC-USER-03.1, TC-USER-02: Ensures that an exception is thrown if the user enters a weak password.")
        public void shouldThrowExceptionWhenPasswordIsWeak() {

            RegisterRequest request = new RegisterRequest("weak@test.com", "WeakUser", "123");

            when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

            // Låtsas att valideringen hittar fel i lösenordet
            when(passwordValidator.validate(request.password())).thenReturn(List.of("Password too short"));

            // Kolla att metoden kastar rätt Exception
            assertThrows(WeakPasswordException.class, () -> {
                authService.registerUser(request);
            });

            // Verifiera att systemet ALDRIG ens försöker spara användaren i databasen
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("TC-USER-02, TC-USER-02.1: Ensures that an exception is thrown when the user tries to register with an email that is already in the database.")
        public void shouldThrowExceptionWhenEmailAlreadyExists() {
            // (HPF-USER-02.1)
            RegisterRequest request = new RegisterRequest("taken@test.com", "NewUser", "ValidPass123!");

            // Simulera att databasen REDAN har en användare med denna e-post
            when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(new User()));

            // Verifiera att rätt fel kastas och användaren inte sparas
            assertThrows(EmailAlreadyExistsException.class, () -> {
                authService.registerUser(request);
            });

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Login Verification Tests")
    class LoginVerificationTests {

        @Test
        @DisplayName("TC-USER-01: Verifies that the user is returned when login is successful.")
        public void shouldReturnUserWhenLoginCredentialsAreCorrect() {
            String plainPassword = "MySecretPassword123!";
            String hashedPassword = "hashed_version_of_password";

            LoginRequest loginRequest = new LoginRequest("user@test.com", plainPassword);

            User mockDbUser = new User();
            mockDbUser.setEmail("user@test.com");
            mockDbUser.setPasswordHash(hashedPassword);

            when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(mockDbUser));
            when(passwordEncoder.matches(plainPassword, hashedPassword)).thenReturn(true);

            User loggedInUser = authService.verifyLogin(loginRequest);


            assertNotNull(loggedInUser);
            assertEquals("user@test.com", loggedInUser.getEmail());
        }

        @Test
        @DisplayName("TC-USER-01: Ensures that an exception is thrown when a login attempt fails.")
        public void shouldThrowExceptionWhenLoginPasswordIsWrong() {
            LoginRequest loginRequest = new LoginRequest("user@test.com", "WrongPassword!");
            User mockDbUser = new User();
            mockDbUser.setPasswordHash("hashed_version_of_password");

            when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(mockDbUser));
            when(passwordEncoder.matches("WrongPassword!", "hashed_version_of_password")).thenReturn(false);


            assertThrows(
                    InvalidLoginCredentialsException.class,
                    () -> authService.verifyLogin(loginRequest),
            "Should throw InvalidLoginCredentialsException when password is wrong");
        }

        @Test
        @DisplayName("TC-USER-01: Verifies that an exception is thrown when the entered email does not exist.")
        public void shouldThrowExceptionWhenLoginEmailDoesNotExist() {
            String nonExistentEmail = "nonexistent@test.com";
            LoginRequest loginRequest = new LoginRequest(nonExistentEmail, "SomePassword123!");

            when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

            InvalidLoginCredentialsException exception = assertThrows(
                    InvalidLoginCredentialsException.class,
                    () -> authService.verifyLogin(loginRequest),
                    "Should throw InvalidLoginCredentialsException when email does not exist"
            );

            assertEquals("Invalid username or password", exception.getMessage());

            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("TC-USER-01: Verifies that an exception is thrown when the password field is null.")
        public void shouldThrowExceptionWhenPasswordIsNull() {
            String email = "user@test.com";
            LoginRequest loginRequest = new LoginRequest(email, null);

            User mockDbUser = new User();
            mockDbUser.setEmail(email);
            mockDbUser.setPasswordHash("hashed_password");

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockDbUser));
            when(passwordEncoder.matches(null, "hashed_password")).thenReturn(false);

            assertThrows(
                    InvalidLoginCredentialsException.class,
                    () -> authService.verifyLogin(loginRequest),
                    "Should throw InvalidLoginCredentialsException when password is null"
            );
        }

        @Test
        @DisplayName("TC-USER-01: Verifies that an exception is thrown when the password is empty.")
        public void shouldThrowExceptionWhenPasswordIsEmpty() {
            String email = "user@test.com";
            String emptyPassword = "";
            LoginRequest loginRequest = new LoginRequest(email, emptyPassword);

            User mockDbUser = new User();
            mockDbUser.setEmail(email);
            mockDbUser.setPasswordHash("hashed_password");

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockDbUser));
            when(passwordEncoder.matches(emptyPassword, "hashed_password")).thenReturn(false);

            assertThrows(
                    InvalidLoginCredentialsException.class,
                    () -> authService.verifyLogin(loginRequest),
                    "Should throw InvalidLoginCredentialsException when password is empty"
            );
        }

        @Test
        @DisplayName("TC-USER-01: Verifies that password encoder is not called on when the email does not exist.")
        public void shouldNotCallPasswordEncoderWhenEmailDoesNotExist() {
            String nonExistentEmail = "nonexistent@test.com";
            LoginRequest loginRequest = new LoginRequest(nonExistentEmail, "SomePassword123!");

            when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

            try {
                authService.verifyLogin(loginRequest);
                fail("Should have thrown InvalidLoginCredentialsException");
            } catch (InvalidLoginCredentialsException e) {
                // Expected exception
            }

            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
    }


    @Nested
    @DisplayName("Helper Method Tests")
    class HelperMethodTests {

        @Test
        @DisplayName("Verifies that findByEmail() returns true when an email is found.")
        public void shouldReturnTrueWhenEmailExists() {
            String email = "existing@test.com";
            User mockUser = new User();
            mockUser.setEmail(email);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

            boolean result = authService.existsByEmail(email);

            assertTrue(result, "Should return true when email exists");

            verify(userRepository, times(1)).findByEmail(email);
        }

        @Test
        @DisplayName("Verifies that findByEmail() returns false when an email is not found.")
        public void shouldReturnFalseWhenEmailDoesNotExist() {
            String nonExistentEmail = "nonexistent@test.com";
            when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

            boolean result = authService.existsByEmail(nonExistentEmail);

            assertFalse(result, "Should return false when email does not exist");

            verify(userRepository, times(1)).findByEmail(nonExistentEmail);
        }

        @Test
        @DisplayName("Verifies that findByEmail() returns the user when they are found by email.")
        public void shouldReturnUserWhenFindByEmailFindsUser() {
            String email = "existing@test.com";
            User mockUser = new User();
            mockUser.setEmail(email);
            mockUser.setDisplayName("Test User");

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

            Optional<User> result = authService.findByEmail(email);

            assertTrue(result.isPresent(), "Optional should contain a user");
            assertEquals(email, result.get().getEmail(), "Email should match");
            assertEquals("Test User", result.get().getDisplayName(), "Display name should match");

            verify(userRepository, times(1)).findByEmail(email);
        }

        @Test
        public void shouldReturnEmptyWhenFindByEmailDoesNotFindUser() {
            String nonExistentEmail = "nonexistent@test.com";
            when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

            Optional<User> result = authService.findByEmail(nonExistentEmail);

            assertFalse(result.isPresent(), "Optional should be empty");

            verify(userRepository, times(1)).findByEmail(nonExistentEmail);
        }
    }
}