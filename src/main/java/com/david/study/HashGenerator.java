package com.david.study;

import java.util.Random;

public class HashGenerator {
    public static String generateRandomHash(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        String hexChars = "0123456789abcdef";
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(hexChars.length());
            sb.append(hexChars.charAt(randomIndex));
        }
        return sb.toString();
    }
}
