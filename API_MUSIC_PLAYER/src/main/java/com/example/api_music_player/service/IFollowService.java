package com.example.api_music_player.service;

import com.example.api_music_player.dto.ChangePasswordRequest;
import com.example.api_music_player.model.Follow;
import com.example.api_music_player.model.User;

import java.util.List;

public interface IFollowService {
    Follow addFollow(Follow follow);
    void unFollow(Follow follow);

    Long countFollowUser(int userId);
    Boolean isExistFollow(Follow follow);

//     dc follow
    List<User> getAllUserFollower(int userId);
//  di follow
    List<User> getAllUserFollowee(int userId);

}
