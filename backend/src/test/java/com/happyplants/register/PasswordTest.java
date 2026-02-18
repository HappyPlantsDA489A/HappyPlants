package com.happyplants.register;

import static org.junit.jupiter.api.Assertions.*;

import com.happyplants.util.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PasswordTest {
    private PasswordValidator passwordValidator;

    @BeforeEach
    public void setUp() {
        passwordValidator = new PasswordValidator();
    }

    @Test
    //All requirements are fulfilled
    public void testValidPassword() {
        boolean result = passwordValidator.isValid("AValidPassword123!");
        assertTrue(result);
    }

    @Test
    //All requirements are fulfilled with exact 12 characters
    public void testValidPasswordBoundary() {
        boolean result = passwordValidator.isValid("!12CharPass!");
        assertTrue(result);
    }

    @Test
    //Missing 1 in length
    public void testInvalidPassLengthBoundary() {
        boolean result = passwordValidator.isValid("NotValid12!");
        assertFalse(result);
    }

    @Test
    //All requirements except for minimum 1 special char are fulfilled
    public void testInvalidPassSpecialCharMissing() {
        boolean result = passwordValidator.isValid("Abcdefg12345");
        assertFalse(result);
    }

    @Test
    //All requirements except for minimum 1 upper case letter are fulfilled
    public void testInvalidPassUpperCaseMissing() {
        boolean result = passwordValidator.isValid("abcdefg1234!");
        assertFalse(result);
    }

    @Test
    //All requirements except for minimum 1 lower case letter are fulfilled
    public void testInvalidPassLowerCaseMissing() {
        boolean result = passwordValidator.isValid("ABCDEFG1234!");
        assertFalse(result);
    }

    @Test
    //All requirements except for minimum 12 characters are fulfilled
    public void testInvalidPassLengthMissing() {
        boolean result = passwordValidator.isValid("Abc123!");
        assertFalse(result);
    }

    @Test
    //No requirement fulfilled
    public void testInvalidPass() {
        boolean result = passwordValidator.isValid("");
        assertFalse(result);
    }


}
