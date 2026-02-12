package com.happyplants.util;

import java.util.List;

public class PasswordValidator {

    public boolean isValid(String password) {
        return validate(password).isEmpty();
    }

    public List<String> validate(String password) {
        List<String> errors = new java.util.ArrayList<>();

        if (password == null) {
            errors.add("Password cannot be null.");
            return errors;
        }

        if (password.length() < 12) {
            errors.add("Password must be at least 12 characters long.");
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c) & !Character.isWhitespace(c)) {
                hasSpecial = true;
            }
        }

        if (!hasUppercase) {
            errors.add("Password must contain at least one uppercase letter.");
        }
        if (!hasLowercase) {
            errors.add("Password must contain at least one lowercase letter.");
        }
        if (!hasDigit) {
            errors.add("Password must contain at least one digit.");
        }
        if (!hasSpecial) {
            errors.add("Password must contain at least one special character.");
        }

        return errors;
    }
}
