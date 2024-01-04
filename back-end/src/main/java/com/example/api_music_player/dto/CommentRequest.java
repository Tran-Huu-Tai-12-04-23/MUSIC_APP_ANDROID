package com.example.api_music_player.dto;


import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
@Data
public class CommentRequest {
    private User user;
    private Song song;
    private String content;


}

