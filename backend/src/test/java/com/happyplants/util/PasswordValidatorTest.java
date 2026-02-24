package com.happyplants.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("PasswordValidator Tests")
public class PasswordValidatorTest {
    private PasswordValidator passwordValidator;

    // Expected error messages
    private static final String ERROR_NULL = "Password cannot be null.";
    private static final String ERROR_BLANK = "Password cannot be empty or contain only whitespace.";
    private static final String ERROR_TOO_LONG = "Password cannot be longer than 100 characters.";
    private static final String ERROR_TOO_SHORT = "Password must be at least 12 characters long.";
    private static final String ERROR_NO_UPPERCASE = "Password must contain at least one uppercase letter.";
    private static final String ERROR_NO_LOWERCASE = "Password must contain at least one lowercase letter.";
    private static final String ERROR_NO_DIGIT = "Password must contain at least one digit.";
    private static final String ERROR_NO_SPECIAL = "Password must contain at least one special character.";

    @BeforeEach
    public void setUp() {
        passwordValidator = new PasswordValidator();
    }

    // ========== Tests for isValid() method ==========

    @Test
    public void shouldReturnTrueWhenAllRequirementsAreMet() {
        boolean result = passwordValidator.isValid("AValidPassword123!");
        assertTrue(result, "Password should be valid when all requirements are met");
    }

    @Test
    public void shouldReturnTrueWhenPasswordIsExactly12Characters() {
        boolean result = passwordValidator.isValid("!12CharPass!");
        assertTrue(result, "Password should be valid at exactly 12 characters with all requirements met");
    }

    @Test
    public void shouldReturnFalseWhenPasswordIsShorterThan12Characters() {
        boolean result = passwordValidator.isValid("NotValid12!");
        assertFalse(result, "Password should be invalid when one character below minimum length");
    }

    @Test
    public void shouldReturnFalseWhenPasswordIsLongerThan100Characters() {
        String password = "A".repeat(101) + "1a!";
        boolean result = passwordValidator.isValid(password);
        assertFalse(result, "Password should be invalid when exceeding maximum length");
    }

    @Test
    public void shouldReturnFalseWhenSpecialCharacterIsMissing() {
        boolean result = passwordValidator.isValid("Abcdefg12345");
        assertFalse(result, "Password should be invalid without special character");
    }

    @Test
    public void shouldReturnFalseWhenUppercaseLetterIsMissing() {
        boolean result = passwordValidator.isValid("abcdefg1234!");
        assertFalse(result, "Password should be invalid without uppercase letter");
    }

    @Test
    public void shouldReturnFalseWhenLowercaseLetterIsMissing() {
        boolean result = passwordValidator.isValid("ABCDEFG1234!");
        assertFalse(result, "Password should be invalid without lowercase letter");
    }

    @Test
    public void shouldReturnFalseWhenPasswordIsBelowMinimumLength() {
        boolean result = passwordValidator.isValid("Abc123!");
        assertFalse(result, "Password should be invalid when below minimum length");
    }

    @Test
    public void shouldReturnFalseWhenPasswordIsEmpty() {
        boolean result = passwordValidator.isValid("");
        assertFalse(result, "Password should be invalid when empty");
    }

    @Test
    public void shouldReturnFalseWhenPasswordIsOnlyWhitespace() {
        boolean result = passwordValidator.isValid(" ");
        assertFalse(result, "Password should be invalid when containing only whitespace");
    }

    @Test
    public void shouldReturnFalseWhenPasswordIsNull() {
        boolean result = passwordValidator.isValid(null);
        assertFalse(result, "Password should be invalid when null");
    }

    // ========== Tests for validate() method ==========

    @Test
    public void shouldReturnNullErrorWhenPasswordIsNull() {
        List<String> errors = passwordValidator.validate(null);
        assertEquals(1, errors.size(), "Null password should return exactly one error");
        assertTrue(errors.contains(ERROR_NULL), "Error list should contain null password message");
    }

    @Test
    public void shouldReturnTooLongErrorWhenPasswordExceeds100Characters() {
        String password = "A".repeat(101) + "1a!";
        List<String> errors = passwordValidator.validate(password);
        assertEquals(1, errors.size(), "Too long password should return exactly one error");
        assertTrue(errors.contains(ERROR_TOO_LONG), "Error list should contain too long password message");
    }

    @Test
    public void shouldReturnMultipleErrorsWhenPasswordHasMultipleViolations() {
        List<String> errors = passwordValidator.validate("abc");
        assertEquals(4, errors.size(), "Password with multiple violations should return all applicable errors");
        assertTrue(errors.contains(ERROR_TOO_SHORT), "Error list should contain too short message");
        assertTrue(errors.contains(ERROR_NO_UPPERCASE), "Error list should contain missing uppercase message");
        assertTrue(errors.contains(ERROR_NO_DIGIT), "Error list should contain missing digit message");
        assertTrue(errors.contains(ERROR_NO_SPECIAL), "Error list should contain missing special character message");
    }

    @Test
    public void shouldReturnLowercaseErrorWhenLowercaseLetterIsMissing() {
        List<String> errors = passwordValidator.validate("ABCDEFG1234!");
        assertEquals(1, errors.size(), "Password without lowercase letter should return exactly one error");
        assertTrue(errors.contains(ERROR_NO_LOWERCASE), "Error list should contain missing lowercase message");
    }

    @Test
    public void shouldReturnBlankErrorWhenPasswordIsEmpty() {
        List<String> errors = passwordValidator.validate("");
        assertEquals(1, errors.size(), "Empty password should return exactly one error");
        assertTrue(errors.contains(ERROR_BLANK), "Error list should contain blank password message");
    }

    @Test
    public void shouldReturnBlankErrorWhenPasswordIsOnlyWhitespace() {
        List<String> errors = passwordValidator.validate("   ");
        assertEquals(1, errors.size(), "Whitespace-only password should return exactly one error");
        assertTrue(errors.contains(ERROR_BLANK), "Error list should contain blank password message");
    }

    @Test
    public void shouldReturnSpecialCharErrorWhenWhitespaceIsUsedAsSpecialChar() {
        List<String> errors = passwordValidator.validate("Abcdefg12345 ");
        assertEquals(1, errors.size(), "Password with whitespace instead of special character should return exactly one error");
        assertTrue(errors.contains(ERROR_NO_SPECIAL), "Error list should contain missing special character message");
    }

    @Test
    public void shouldReturnNoErrorsWhenPasswordIsExactly12Characters() {
        String password = "Abcdefg123!@";
        List<String> errors = passwordValidator.validate(password);
        assertEquals(0, errors.size(), "Password with exactly 12 characters meeting all requirements should have no errors");
    }

    @Test
    public void shouldReturnNoErrorsWhenPasswordIsExactly100Characters() {
        String password = "A1@" + "b".repeat(97);
        List<String> errors = passwordValidator.validate(password);
        assertEquals(0, errors.size(), "Password with exactly 100 characters meeting all requirements should have no errors");
    }
}
