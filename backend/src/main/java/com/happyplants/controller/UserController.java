package com.happyplants.controller;

import com.happyplants.dto.ChangePasswordRequest;
import com.happyplants.dto.UserInfoResponse;
import com.happyplants.model.User;
import com.happyplants.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping
    @Operation(summary = "Delete user account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAccount(Authentication auth) {
        User user = userService.getCurrentUser(auth);
        userService.deleteUser(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user-info")
    @Operation(summary = "Get current user info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserInfoResponse> getUserInfo(Authentication auth) {
        User user = userService.getCurrentUser(auth);
        UserInfoResponse response = new UserInfoResponse(user.getEmail(), user.getDisplayName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/change-password")
    @Operation(summary = "Change password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication auth) {

        User user = userService.getCurrentUser(auth);

        userService.changePassword(user, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");

        return ResponseEntity.ok(response);
    }
}
