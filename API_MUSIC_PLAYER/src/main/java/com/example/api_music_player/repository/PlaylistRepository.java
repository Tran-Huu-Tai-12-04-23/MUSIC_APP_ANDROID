package com.example.api_music_player.repository;

import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Playlist findByTitle(String title);

    @Override
    Page<Playlist> findAll(Pageable pageable);

    List<Playlist> findAllByIsPrivateFalse(Pageable pageable);

    List<Playlist> findAllByUserId(Pageable pageable, int userId);


    @Query("SELECT p FROM Playlist p " +
            "WHERE p.user.id = :userId " +
            "AND NOT EXISTS (SELECT 1 FROM DetailPlaylist dp WHERE dp.playlist.id = p.id AND dp.song.id = :songId)")
    List<Playlist> findAllWithoutSongByUserIdAndSongId(@Param("userId") Long userId, @Param("songId") Long songId);



}
