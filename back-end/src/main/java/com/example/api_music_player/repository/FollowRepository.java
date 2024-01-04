package com.example.api_music_player.repository;

import com.example.api_music_player.model.Follow;
import com.example.api_music_player.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Transactional
    void deleteByFollowerIdAndFolloweeId(int followerId, int followeeId);
    long countByFolloweeId(int followeeId);

    @Query("SELECT u FROM User u JOIN Follow f ON u.id = f.follower.id WHERE f.followee.id = :userId")
    List<User> getFollowers(@Param("userId") int userId);

    @Query("SELECT u FROM User u JOIN Follow f ON u.id = f.followee.id WHERE f.follower.id = :userId")
    List<User> getAllFollowee(@Param("userId") int userId);


    List<Follow> findAllByFolloweeIdAndFollowerId(int followeeId, int followerId);


}
