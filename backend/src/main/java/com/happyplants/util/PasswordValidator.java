package com.happyplants.util;

import java.util.List;

public class PasswordValidator {

    private static final int MIN_LENGTH = 12;
    private static final int MAX_LENGTH = 100;

    public boolean isValid(String password) {
        return validate(password).isEmpty();
    }

    public List<String> validate(String password) {
        List<String> errors = new java.util.ArrayList<>();

        if (password == null) {
            errors.add("Password cannot be null.");
            return errors;
        }

        if (password.length() > MAX_LENGTH) {
            errors.add("Password cannot be longer than 100 characters.");
            return errors;
        }

        if (password.length() < MIN_LENGTH) {
            errors.add("Password must be at least 12 characters long.");
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            }

            if (Character.isLowerCase(c)) {
                hasLowercase = true;
            }

            if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) {
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
