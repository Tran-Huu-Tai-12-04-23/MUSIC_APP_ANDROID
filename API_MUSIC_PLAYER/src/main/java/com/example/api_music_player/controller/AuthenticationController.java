package com.example.api_music_player.controller;


import com.example.api_music_player.dto.ChangePasswordRequest;
import com.example.api_music_player.dto.Response;
import com.example.api_music_player.exception.PhoneNumberInUseException;
import com.example.api_music_player.exception.UserAlreadyExistsException;
import com.example.api_music_player.model.User;
import com.example.api_music_player.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                    System.out.println("failed register!");
                    return ResponseEntity.badRequest().body("Register failed!");
                }

                System.out.println(userRes.getUsername());
                Response<User> response = new Response<>(userRes, "Register successfully!");
                return ResponseEntity.ok(response);
            }

        }  catch (UserAlreadyExistsException | PhoneNumberInUseException e) {
            Response<User> response = new Response<>();
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response<User> response = new Response<>();
            response.setMessage("Register failed!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
            }
            User userRes = userService.login(user);

            if( userRes == null ) {
                Response<User> response = new Response<>();
                response.setMessage("Không tìm thấy tài khoản!");
                return ResponseEntity.ok(response);
            }
            Response<User> response = new Response<>(userRes, "Đăng nhập thành công!");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Response<User> response = new Response<>(null, e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/all-user")
    public ResponseEntity<?> getAllUser() {
        List<User> data = userService.getAll();
        return ResponseEntity.badRequest().body(data);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try{
            Response<User> response = new Response<>();
            User user = userService.changePassword(changePasswordRequest);
            response.setData(user);
            return ResponseEntity.ok(response);
        }catch (RuntimeException e) {
            Response<User> response = new Response<>();
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}
