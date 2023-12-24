package com.example.api_music_player.util;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.*;
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
    public static File downloadFile(String url) throws IOException {
        System.out.println(url);
        File tempFile = File.createTempFile("audio", ".mp3");

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url));
             InputStream in = response.getEntity().getContent();
             OutputStream out = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }


    public static boolean verifyPassword(String password, String hashedPassword) {
        String newHashedPassword = hashPassword(password);
        return newHashedPassword.equals(hashedPassword);
    }

    public static int getAudioDuration(String url ) throws IOException, CannotReadException, TagException, InvalidAudioFrameException {
        try {
            File audioFile = downloadFile(url);
            AudioFile file = AudioFileIO.read(audioFile);
            int duration = file.getAudioHeader().getTrackLength();
            return duration;
        } catch (IOException | CannotReadException | TagException | InvalidAudioFrameException e) {
            e.printStackTrace();
            throw e;
        } catch (ReadOnlyFileException e) {
            throw new RuntimeException(e);
        }
    }
}
