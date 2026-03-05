package com.happyplants.controller;

import com.happyplants.dto.ChangePasswordRequest;
import com.happyplants.model.User;
import com.happyplants.dto.LoginRequest;
import com.happyplants.dto.RegisterRequest;
import com.happyplants.service.AuthService;
import com.happyplants.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    // Error handling is in service method and exception/GlobalExceptionHandler.java
    @PostMapping("/log-in")
    @Operation(summary = "Log in")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        User user = authService.verifyLogin(loginRequest);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getId(),
                null,
                java.util.Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful");

        return ResponseEntity.ok(response);
    }

    // Error handling is in service method and exception/GlobalExceptionHandler.java
    @PostMapping("/register")
    @Operation(summary = "Register")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User savedUser = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @DeleteMapping("/log-out")
    @Operation(summary = "Log out")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse httpResponse) {
        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("HAPPY_COOKIE", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);

        httpResponse.addCookie(cookie);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-auth")
    @Operation(summary = "Check auth status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> testSession(Authentication auth) {
        return ResponseEntity.ok("Logged in as: " + auth.getName());
    }

    @PatchMapping("/change-password")
    @Operation(summary = "Change password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication auth) {

        User user = userService.getCurrentUser(auth);

        authService.changePassword(user, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");

        return ResponseEntity.ok(response);
    }
}