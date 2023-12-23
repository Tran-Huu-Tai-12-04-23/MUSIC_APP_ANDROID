package com.example.api_music_player.controller;


import com.example.api_music_player.dto.HistoryRequest;
import com.example.api_music_player.dto.HomeResponse;
import com.example.api_music_player.dto.Response;
import com.example.api_music_player.model.History;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import com.example.api_music_player.service.IHistoryService;
import com.example.api_music_player.service.ISongService;
import com.example.api_music_player.service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {

    private final IHistoryService iHistoryService;

    @PostMapping
    public ResponseEntity<?> addHistory(@RequestBody HistoryRequest historyRequest) {
        return ResponseEntity.ok(iHistoryService.addHistory(historyRequest));
    }

    @PutMapping("/remove")
    public ResponseEntity<?> removeHistory(@RequestBody HistoryRequest historyRequest) {
        return ResponseEntity.ok(iHistoryService.removeHistory(historyRequest));
    }

    @DeleteMapping("/remove-all/{userId}")
    public ResponseEntity<?> removeAllHistoryUser(@PathVariable int userId) {
        return ResponseEntity.ok(iHistoryService.removeAllHistoryByUserId(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getHistoryByUser(
            @PathVariable Long userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        try{
            Response<List<Song>> response = new Response<>();
            List<Song> data = iHistoryService.getHistoryListened(userId, page, size);
            response.setData(data);
            response.setMessage("Get history successfully!");
            return ResponseEntity.ok(response);
        }catch (Exception e ) {
            Response<Song> response = new Response<>();
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }


    }
}
