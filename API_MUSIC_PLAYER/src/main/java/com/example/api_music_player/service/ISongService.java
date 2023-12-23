package com.example.api_music_player.service;

import com.example.api_music_player.model.Liked;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;

import java.util.List;

public interface ISongService {
    Song create(Song song);
    Liked likeSong(Liked liked);
    Boolean unLikeSong(Liked liked);
    Boolean isCheckUserLikeSong(Liked liked);
    Song update(Song song);

    Song getSongById(Long id);
    List<Song> getAll();
    boolean remove(Long id);

    List<Song> getAllWithPageSize(int page, int size);

    List<Song> getNewSong(int page, int size);
    List<Song> getFamousSong(int page, int size);
    List<Song> getRecentSong(int page, int size);
    List<Playlist> getRecentPlaylist(int page, int size);
    List<Song> search(int page, int size, String key);
    List<Song> getSongLikesByUserid(Long userId, Integer page, Integer size);
    List<Song> getAllSongByUser(int userId, Integer page, Integer size);
    List<Song> getAllSongByUserPrivate(int userId, Integer page, Integer size);
    List<Song> getAllSongByUserPublic(int userId, Integer page, Integer size);

}
