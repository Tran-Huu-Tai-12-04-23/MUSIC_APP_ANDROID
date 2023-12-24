package com.example.api_music_player.repository;

import com.example.api_music_player.model.Profile;
import com.example.api_music_player.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUserId(int userId);

}
