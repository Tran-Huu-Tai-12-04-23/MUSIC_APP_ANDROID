package com.example.api_music_player.service.impl;

import com.example.api_music_player.model.Follow;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import com.example.api_music_player.repository.FollowRepository;
import com.example.api_music_player.repository.LikeRepository;
import com.example.api_music_player.repository.PlaylistRepository;
import com.example.api_music_player.repository.SongRepository;
import com.example.api_music_player.service.IFollowService;
import com.example.api_music_player.service.ISongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService implements IFollowService {
    private final FollowRepository followRepository;

    @Override
    public Follow addFollow(Follow follow) {
        return followRepository.save(follow);
    }

    @Override
    public void unFollow(Follow follow) {
        followRepository.deleteByFollowerIdAndFolloweeId(follow.getFollower().getId(), follow.getFollowee().getId());
    }

    @Override
    public Long countFollowUser(int userId) {
        return followRepository.countByFolloweeId(userId);
    }

    @Override
    public Boolean isExistFollow(Follow follow) {
        if( follow.getFollower() == null || follow.getFollowee() == null) throw new RuntimeException("Invalid data!");

        List<Follow> followExist = followRepository.findAllByFolloweeIdAndFollowerId( follow.getFollowee().getId(), follow.getFollower().getId());
        return !followExist.isEmpty();
    }
    @Override
    public List<User> getAllUserFollower(int userId) {
        return followRepository.getFollowers(userId);
    }

    @Override
    public List<User> getAllUserFollowee(int userId) {
        return followRepository.getAllFollowee(userId);
    }
}
