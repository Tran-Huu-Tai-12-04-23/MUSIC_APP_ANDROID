package com.example.api_music_player.controller;


import com.example.api_music_player.dto.HomeResponse;
import com.example.api_music_player.dto.Response;
import com.example.api_music_player.model.*;
import com.example.api_music_player.service.ISongService;
import com.example.api_music_player.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService iUserService;
    @PostMapping("/profile/edit")
    public ResponseEntity<?> editProfile(@RequestBody Profile profile) {
        return ResponseEntity.ok(iUserService.editProfile(profile));
    }

    @GetMapping("/profile/{userID}")
    public ResponseEntity<?> getUserProfile(@PathVariable int userID) {
        System.out.println(userID);
        return ResponseEntity.ok(iUserService.getProfileByUser(userID));
    }


}
