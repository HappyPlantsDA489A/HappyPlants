package com.happyplants.controller;

import com.happyplants.model.User;
import com.happyplants.model.dto.LoginRequest;
import com.happyplants.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/log-in")
    public ResponseEntity<String> loginOnlyByEmail(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        User user = authService.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by passed email"));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getId(),
                null,
                java.util.Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok("Login successful");
    }

    @GetMapping("/check-auth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> testSession(Authentication auth) {
        return ResponseEntity.ok("Logged in as: " + auth.getName());
    }
}