package com.happyplants.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for changing password.
 * Contains the current password for validation and the new password.
 */
public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        String newPassword
) {}