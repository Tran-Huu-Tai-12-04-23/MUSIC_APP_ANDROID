package com.example.api_music_player.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Util {
    // Use a secure random salt for added security
    private static final SecureRandom secureRandom = new SecureRandom();

    // Number of iterations for password hashing
    private static final int ITERATIONS = 10000;
    private final static String salt = "10";

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] saltBytes = Base64.getDecoder().decode(Util.salt);
            md.update(saltBytes);
            byte[] passwordBytes = password.getBytes();
            md.update(passwordBytes);

            byte[] hashedPassword = md.digest();
            for (int i = 0; i < ITERATIONS - 1; i++) {
                md.reset();
                hashedPassword = md.digest(hashedPassword);
            }

            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available.");
        }
    }

    public static String generateSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        String newHashedPassword = hashPassword(password);
        return newHashedPassword.equals(hashedPassword);
    }
}
