package com.happyplants.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {
    @PostMapping("/log-in")
    public ResponseEntity<String> loginDummy(HttpServletRequest request) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user@happyplants.se",
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