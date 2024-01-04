package DTO;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import Model.Playlist;
import Model.Song;
import Model.User;

public class HomeResponse {
    @SerializedName("newestSong")
    private ArrayList<Song> newestSong;

    @SerializedName("recentMusic")
    private ArrayList<Song> recentMusic;

    @SerializedName("artistList")
    private ArrayList<User> artistList;

    @SerializedName("famousSong")
    private ArrayList<Song> famousSong;

    @SerializedName("famousPlaylist")
    private ArrayList<Playlist> famousPlaylist;

    public ArrayList<Song> getNewestSong() {
        return newestSong;
    }

    public void setNewestSong(ArrayList<Song> newestSong) {
        this.newestSong = newestSong;
    }

    public ArrayList<Song> getRecentMusic() {
        return recentMusic;
    }

    public void setRecentMusic(ArrayList<Song> recentMusic) {
        this.recentMusic = recentMusic;
    }

    public ArrayList<User> getArtistList() {
        return artistList;
    }

    public void setArtistList(ArrayList<User> artistList) {
        this.artistList = artistList;
    }

    public ArrayList<Song> getFamousSong() {
        return famousSong;
    }

    public void setFamousSong(ArrayList<Song> famousSong) {
        this.famousSong = famousSong;
    }

    public ArrayList<Playlist> getFamousPlaylist() {
        return famousPlaylist;
    }

    public void setFamousPlaylist(ArrayList<Playlist> famousPlaylist) {
        this.famousPlaylist = famousPlaylist;
    }
}
