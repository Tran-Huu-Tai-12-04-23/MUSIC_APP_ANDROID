package com.example.api_music_player.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response<I> {
    private I data;
    private String message;

    public Response(I data, String message) {
        this.data = data;
        this.message = message;
    }

    public Response() {
    }
}
