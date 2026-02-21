package com.happyplants.service;

import com.happyplants.exception.WeakPasswordException;
import com.happyplants.model.User;
import com.happyplants.model.dto.RegisterRequest;
import com.happyplants.repository.UserRepository;
import com.happyplants.util.PasswordValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.happyplants.exception.EmailAlreadyExistsException;
import com.happyplants.exception.InvalidLoginCredentialsException;
import com.happyplants.model.dto.LoginRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Detta gör att vi kan använda Mockito utan hela Spring Boot
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordValidator passwordValidator;

    @InjectMocks
    private AuthService authService;

    @Test
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

    @Test
    public void shouldReturnUserWhenLoginCredentialsAreCorrect() {
        //(Testar "A successfully registered user can log in...")
        String plainPassword = "MySecretPassword123!";
        String hashedPassword = "hashed_version_of_password";

        LoginRequest loginRequest = new LoginRequest("user@test.com", plainPassword);

        User mockDbUser = new User();
        mockDbUser.setEmail("user@test.com");
        mockDbUser.setPasswordHash(hashedPassword);

        // Simulera att databasen hittar användaren
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(mockDbUser));
        // Simulera att lösenorden matchar
        when(passwordEncoder.matches(plainPassword, hashedPassword)).thenReturn(true);

        User loggedInUser = authService.verifyLogin(loginRequest);


        assertNotNull(loggedInUser);
        assertEquals("user@test.com", loggedInUser.getEmail());
    }

    @Test
    public void shouldThrowExceptionWhenLoginPasswordIsWrong() {
        LoginRequest loginRequest = new LoginRequest("user@test.com", "WrongPassword!");
        User mockDbUser = new User();
        mockDbUser.setPasswordHash("hashed_version_of_password");

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(mockDbUser));
        when(passwordEncoder.matches("WrongPassword!", "hashed_version_of_password")).thenReturn(false);


        assertThrows(InvalidLoginCredentialsException.class, () -> {
            authService.verifyLogin(loginRequest);
        });
    }


    //TILLS UTVECKLARNA FIXAT KODEN (HPF-USER-02.2.1)
    /*
    @Test
    public void shouldThrowExceptionWhenDisplayNameAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("new@test.com", "TakenName", "ValidPass123!");

        // Låtsas att e-posten är ledig
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        // MEN låtsas att visningsnamnet redan är upptaget av någon annan
        when(userRepository.findByDisplayName(request.displayName())).thenReturn(Optional.of(new User()));

        // Act & Assert - Verifiera att rätt fel kastas (när utvecklarna väl skapat det)
        assertThrows(DisplayNameAlreadyExistsException.class, () -> {
            authService.registerUser(request);
        });

        // Verifiera att användaren stoppas från att sparas i databasen
        verify(userRepository, never()).save(any(User.class));
    }
    */
}