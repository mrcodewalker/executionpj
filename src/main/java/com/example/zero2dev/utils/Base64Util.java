package com.example.zero2dev.utils;

import java.util.Base64;

public class Base64Util {
    public static String encodeBase64(String input) {
        try {
            return Base64.getEncoder().encodeToString(input.getBytes());
        } catch (Exception e) {
            System.err.println("Error encoding to Base64: " + e.getMessage());
            return null;
        }
    }

    public static String decodeBase64(String base64String) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            return new String(decodedBytes);
        } catch (Exception e) {
            System.err.println("Error decoding from Base64: " + e.getMessage());
            return null;
        }
    }
}
