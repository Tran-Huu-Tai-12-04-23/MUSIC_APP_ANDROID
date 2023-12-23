package com.example.api_music_player.controller;


import com.example.api_music_player.dto.DetailPlaylistRequest;
import com.example.api_music_player.dto.HomeResponse;
import com.example.api_music_player.dto.Response;
import com.example.api_music_player.model.DetailPlaylist;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import com.example.api_music_player.service.IPlaylistService;
import com.example.api_music_player.service.ISongService;
import com.example.api_music_player.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/playlist")
@RequiredArgsConstructor
public class PlaylistController {
    private final IPlaylistService iPlaylistService;
    @PostMapping("/add")
    public ResponseEntity<?> addPlaylist(@RequestBody Playlist playlist) {
        Response<Playlist> response = new Response<>();
        try{
            Playlist playlistNew = iPlaylistService.create(playlist);
            response.setData(playlistNew);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e ) {
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllPlaylistByUser(
            @PathVariable int userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        Response<List<Playlist>> response = new Response<>();
        try{
            List<Playlist> playlistNew = iPlaylistService.getAllPlaylistByUserId(page, size, userId);
            response.setData(playlistNew);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e ) {
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    @GetMapping("/all/private/{userId}")
    public ResponseEntity<?> getAllPrivatePlaylistByUser(
            @PathVariable int userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        Response<List<Playlist>> response = new Response<>();
        try{
            List<Playlist> playlistNew = iPlaylistService.getAllPlaylistPrivateByUserId(page, size, userId);
            response.setData(playlistNew);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e ) {
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/all/public/{userId}")
    public ResponseEntity<?> getAllPublicPlaylistByUser(
            @PathVariable int userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        Response<List<Playlist>> response = new Response<>();
        try{
            List<Playlist> playlistNew = iPlaylistService.getAllPlaylistPublicByUserId(page, size, userId);
            response.setData(playlistNew);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e ) {
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/detail/{playlistId}")
    public ResponseEntity<?> getAllPlaylistByUser(
            @PathVariable Long playlistId
    ) {
        Response<List<DetailPlaylist>> response = new Response<>();
        try{
            List<DetailPlaylist> playlistNew = iPlaylistService.getDetailPlaylistById(playlistId);
            response.setData(playlistNew);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e ) {
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    @DeleteMapping("/remove/{playlistId}")
    public ResponseEntity<?> removePlaylist(
            @PathVariable Long playlistId
    ) {
        iPlaylistService.removeById(playlistId);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/add/song")
    public ResponseEntity<?> addSongPlaylist(@RequestBody DetailPlaylistRequest detailPlaylistRequest) {
        Response<Playlist> response = new Response<>();
        try{
            Playlist playlistNew = iPlaylistService.addSongIntoPlaylist(detailPlaylistRequest.getPlaylistId(), detailPlaylistRequest.getSongId());
            response.setData(playlistNew);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e ) {
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/remove/song/{detailPlaylistId}")
    public ResponseEntity<?> removeSongPlaylist(@PathVariable int detailPlaylistId) {
        iPlaylistService.removeSongPlaylist(detailPlaylistId);
        return ResponseEntity.ok(true);
    }


    @GetMapping("/all/not-exist-song/{userId}")
    public ResponseEntity<?> getAllPlaylistByUserNotHaveSong(
            @PathVariable int userId,
            @RequestParam long songId

    ) {
        Response<List<Playlist>> response = new Response<>();
        try{
            List<Playlist> playlistNew = iPlaylistService.getAllPlaylistByUserIdNotHaveSong( userId, songId);
            response.setData(playlistNew);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e ) {
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

}
