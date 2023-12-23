package com.example.api_music_player.dto;

import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HomeResponse {
    private List<Song> newestSong;
    private List<Song> recentMusic;
    private List<User> artistList;
    private List<Song> famousSong;
    private List<Playlist> famousPlaylist;
}
