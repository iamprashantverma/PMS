package com.pms.userservice.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OtpUtil {

    private static final SecureRandom random = new SecureRandom();
    private static final String DIGITS = "0123456789";
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateCustomOtp() {
        List<Character> otpChars = new ArrayList<>();

        // Add 4 random digits
        for (int i = 0; i < 4; i++) {
            otpChars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }

        // Add 2 random uppercase letters
        for (int i = 0; i < 2; i++) {
            otpChars.add(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }

        // Shuffle to mix letters and digits
        Collections.shuffle(otpChars);

        // Convert to String
        StringBuilder otp = new StringBuilder();
        for (char ch : otpChars) {
            otp.append(ch);
        }

        return otp.toString();
    }
}
