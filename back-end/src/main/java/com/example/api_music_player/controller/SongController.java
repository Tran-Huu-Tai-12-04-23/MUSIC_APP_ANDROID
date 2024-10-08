package com.example.api_music_player.controller;


import com.example.api_music_player.dto.HomeResponse;
import com.example.api_music_player.dto.Response;
import com.example.api_music_player.model.Liked;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import com.example.api_music_player.service.ISongService;
import com.example.api_music_player.service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.TagException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/song")
@RequiredArgsConstructor
public class SongController {
    private final ISongService iSongService;
    private final IUserService iUserService;
    @PostMapping
    public ResponseEntity<?> addSong(@RequestBody Song song)  {
        try {
            return ResponseEntity.ok(iSongService.create(song));
        } catch (CannotReadException | TagException | InvalidAudioFrameException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

        @DeleteMapping("/{songId}")
    public ResponseEntity<?> removeSong(@PathVariable Long songId)  {
        return ResponseEntity.ok(iSongService.remove(songId));
    }


    @PutMapping("/change-scope/{songId}")
    public ResponseEntity<?> changeScope(@PathVariable Long songId, @RequestParam Boolean isPrivate)  {
        return ResponseEntity.ok(iSongService.changeScope(songId, isPrivate));
    }

    @GetMapping()
    public ResponseEntity<?> getAllSong(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        List<Song> data = iSongService.getAllWithPageSize(page, size);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSong(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "key", required = false, defaultValue = "") String key,
            @RequestParam(name = "size", required = false, defaultValue = "12") Integer size
    ) {
        List<Song> data = iSongService.search(page, size,key);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/home")
    public ResponseEntity<?> getHome() {
        HomeResponse homeResponse = new HomeResponse();
        List<Song> newSong = iSongService.getNewSong(0, 20);
        List<Song> famousSong = iSongService.getFamousSong(0, 20);
        List<Song> recentSong = iSongService.getRecentSong(0, 20);
        List<User> famousArtist = iUserService.getFamousArtist(0, 20);
        List<Playlist> playlists = iSongService.getRecentPlaylist(0, 20);
        homeResponse.setNewestSong(newSong);
        homeResponse.setFamousSong(famousSong);
        homeResponse.setRecentMusic(recentSong);
        homeResponse.setArtistList(famousArtist);
        homeResponse.setFamousPlaylist(playlists);
        return ResponseEntity.ok(homeResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e) {
        // Log the exception
        e.printStackTrace();
        // Return an error response
        Response errorResponse = new Response("An error occurred", "detail");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @GetMapping("liked/{userId}")
    public ResponseEntity<?> getLikedSongByUser(
            @PathVariable Long userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        try{
            Response<List<Song>> response = new Response<>();
            List<Song> data = iSongService.getSongLikesByUserid(userId, page, size);
            response.setData(data);
            response.setMessage("Get liked successfully!");
            return ResponseEntity.ok(response);
        }catch (Exception e ) {
            Response<Song> response = new Response<>();
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("public/{userId}")
    public ResponseEntity<?> getAllSongPublicByUser(
            @PathVariable int userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        try{
            Response<List<Song>> response = new Response<>();
            List<Song> data = iSongService.getAllSongByUserPublic(userId, page, size);
            response.setData(data);
            response.setMessage("Get all song public successfully!");
            return ResponseEntity.ok(response);
        }catch (Exception e ) {
            Response<Song> response = new Response<>();
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("private/{userId}")
    public ResponseEntity<?> getAllSongPrivateByUser(
            @PathVariable int userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        try{
            Response<List<Song>> response = new Response<>();
            List<Song> data = iSongService.getAllSongByUserPrivate(userId, page, size);
            response.setData(data);
            response.setMessage("Get all song private successfully!");
            return ResponseEntity.ok(response);
        }catch (Exception e ) {
            Response<Song> response = new Response<>();
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("all/{userId}")
    public ResponseEntity<?> getAllSongByUser(
            @PathVariable int userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        try{
            Response<List<Song>> response = new Response<>();
            List<Song> data = iSongService.getAllSongByUser(userId, page, size);
            response.setData(data);
            response.setMessage("Get all song successfully!");
            return ResponseEntity.ok(response);
        }catch (Exception e ) {
            Response<Song> response = new Response<>();
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // like
    @PostMapping("/like")
    public ResponseEntity<?> like(@RequestBody Liked liked) {
        return ResponseEntity.ok(iSongService.likeSong(liked));
    }

    @PostMapping("/un-like")
    public ResponseEntity<?> unLike(@RequestBody Liked liked) {
        return ResponseEntity.ok(iSongService.unLikeSong(liked));
    }

    @PostMapping("/is-check-user-like")
    public ResponseEntity<?> isCheckUserLikeSong(@RequestBody Liked liked) {
        return ResponseEntity.ok(iSongService.isCheckUserLikeSong(liked));
    }

}
