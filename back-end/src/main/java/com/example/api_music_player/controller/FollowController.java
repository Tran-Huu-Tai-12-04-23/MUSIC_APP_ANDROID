package com.example.api_music_player.controller;


import com.example.api_music_player.dto.HomeResponse;
import com.example.api_music_player.dto.Response;
import com.example.api_music_player.model.Follow;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import com.example.api_music_player.service.IFollowService;
import com.example.api_music_player.service.ISongService;
import com.example.api_music_player.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
public class FollowController {
    private final IFollowService iFollowService;
    @PostMapping("")
    public ResponseEntity<?> follow(@RequestBody Follow follow) {
        System.out.println(follow.getFollower().getId());
        System.out.println(follow.getFollowee().getId());
        return ResponseEntity.ok(iFollowService.addFollow(follow));
    }

    @PostMapping("/check-exist-follow")
    public ResponseEntity<?> isExistFollow(@RequestBody Follow follow) {
        return ResponseEntity.ok(iFollowService.isExistFollow(follow));
    }

    @PostMapping("/un-follow")
    public ResponseEntity<?> unFollow(@RequestBody Follow follow) {
        iFollowService.unFollow(follow);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/follower/{userId}")
    public ResponseEntity<?> getAllFollower(
            @PathVariable int userId
    ) {
        return ResponseEntity.ok(iFollowService.getAllUserFollower(userId));
    }

    @GetMapping("/followee/{userId}")
    public ResponseEntity<?> getAllFollowee(@PathVariable int userId) {
        return ResponseEntity.ok(iFollowService.getAllUserFollowee(userId));
    }
}
