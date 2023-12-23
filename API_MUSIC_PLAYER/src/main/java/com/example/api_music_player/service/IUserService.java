package com.example.api_music_player.service;

import com.example.api_music_player.dto.ChangePasswordRequest;
import com.example.api_music_player.model.User;

import java.util.List;

public interface IUserService {
    User login(User user);
    User register(User user);

    User changePassword(ChangePasswordRequest changePasswordRequest);
    User getUserById(Long id);
    List<User> getAll();
    boolean deActive(Long id);
    boolean active(Long id);

    List<User> getFamousArtist( int page,int size);
}
