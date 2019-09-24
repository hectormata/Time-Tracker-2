package com.main.java.task.Utils.SecurityUtils;

import org.apache.commons.lang3.RandomStringUtils;

public class AuthUtils {

    public static boolean validatePassword(String password) {

        if (password != null && password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            return true;
        }

        return false;
    }

    public static String OTP(int len) {

        String numbers = "0123456789#$&*!";
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        return RandomStringUtils.random(len, alpha + numbers);
    }

    /**
     * Test Client
     */

    public static void main (String[] args) {

        String passwd = "Password123&";
        System.out.println(validatePassword(OTP(9)));
        System.out.println(validatePassword(passwd));
    }
}
