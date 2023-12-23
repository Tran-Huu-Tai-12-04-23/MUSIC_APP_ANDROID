package com.example.api_music_player.exception;


public class PhoneNumberInUseException extends RuntimeException {
    public PhoneNumberInUseException(String message) {
        super(message);
    }
}