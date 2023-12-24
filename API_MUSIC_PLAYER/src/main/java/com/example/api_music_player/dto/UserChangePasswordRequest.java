package com.example.api_music_player.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserChangePasswordRequest {
    private int userId;
    private String password;
    private String confirmPassword;
    private String oldPassword;
}
