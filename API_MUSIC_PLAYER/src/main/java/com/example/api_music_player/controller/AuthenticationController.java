package com.example.api_music_player.controller;


import com.example.api_music_player.dto.Response;
import com.example.api_music_player.model.User;
import com.example.api_music_player.service.IUserService;
import com.example.api_music_player.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final IUserService userService;
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        try {
            String message = "";
            boolean checkErr = false;

            if( user == null)  return (ResponseEntity<?>) ResponseEntity.badRequest();;

            if( user.getUsername().equals(" ")) {
                message = "Please enter username";
                checkErr = true;
            }else if(user.getPassword().equals(" ")) {
                message = "Please enter your password";
                checkErr = true;
            }

            if( checkErr ) {
                Response<User> response = new Response<>();
                response.setMessage(message);
                return ResponseEntity.badRequest().body(response);
            }else {
                User userRes = userService.register(user);

                if( userRes == null ) {
                    return ResponseEntity.badRequest().body("Register failed!");
                }
                Response<User> response = new Response<>(userRes, "Register successfully!");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody User user) {
        try {
            String message = "";
            boolean checkErr = false;


            System.out.println(user.getUsername());
            if( user.getUsername().equals(" ")) {
                message = "Please enter username";
                checkErr = true;
            }else if(user.getPassword().equals(" ")) {
                message = "Please enter your password";
                checkErr = true;
            }

            if( checkErr ) {
                Response<User> response = new Response<>();
                response.setMessage(message);
                return ResponseEntity.badRequest().body(response);
            }else {
                User userRes = userService.login(user);
                Response<User> response = new Response<>(userRes, "Login successfully!");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all-user")
    public ResponseEntity<?> getAllUser() {
        List<User> data = userService.getAll();
        return ResponseEntity.badRequest().body(data);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserById(@RequestParam Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
