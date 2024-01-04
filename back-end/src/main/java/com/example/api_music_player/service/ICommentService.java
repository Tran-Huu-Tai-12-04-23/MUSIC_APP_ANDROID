package com.example.api_music_player.service;

import com.example.api_music_player.model.Comment;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;

import java.util.List;

public interface ICommentService {
    Comment create(Comment comment);
    Comment update(Comment comment);

    void deleteById(int id);
    List<Comment> getAllCommentBySong(int page, int size, long songId);
}
