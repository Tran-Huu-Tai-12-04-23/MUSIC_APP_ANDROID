package com.example.api_music_player.service;

import com.example.api_music_player.model.DetailPlaylist;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;

import java.util.List;

public interface IPlaylistService {
    Playlist create(Playlist playlist);
    Playlist update(Playlist playlist);

    void removeById(Long playlistId);

    List<Playlist> getAllPlaylistByUserId(int page, int size, int userId);
    List<Playlist> getAllPlaylistPrivateByUserId(int page, int size, int userId);
    List<Playlist> getAllPlaylistPublicByUserId(int page, int size, int userId);
    List<Playlist> getAllPlaylistByUserIdNotHaveSong( int userId, long songId);

    List<DetailPlaylist> getDetailPlaylistById(Long playListId);

    Playlist addSongIntoPlaylist(Long playlistId, long songId);
    void removeSongPlaylist(int detailPlaylistId);

}
