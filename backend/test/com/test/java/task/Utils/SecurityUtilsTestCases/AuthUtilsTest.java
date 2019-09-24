package com.test.java.task.Utils.SecurityUtilsTestCases;

import org.junit.jupiter.api.Test;

import static com.main.java.task.Utils.SecurityUtils.AuthUtils.OTP;
import static com.main.java.task.Utils.SecurityUtils.AuthUtils.validatePassword;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthUtilsTest {

    @Test
    public void checkNotValidPasswd() {
        String password = "Pasw1&";
        assertEquals(false, validatePassword(password));
    }
    @Test
    public void checkSmallestPossibleString() {

        String password = "Passw12&";
        assertTrue(validatePassword(password));
    }
    @Test
    public void checkPasswordValidation() {

        String password = "Password123&";
        assertTrue(validatePassword(password));
    }

    @Test
    public void checkLenOfRandomString() {

        int len = 8;
        String str = OTP(8);
        int actual = str.length();

        assertEquals(len, actual);

    }
}
