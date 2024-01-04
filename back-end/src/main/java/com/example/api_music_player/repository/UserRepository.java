package com.example.api_music_player.repository;

import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String name);

    User findByPhoneNumber(String phoneNumber);

    List<User> findAllByTotalFollowNotNull(  Pageable pageable);


}
