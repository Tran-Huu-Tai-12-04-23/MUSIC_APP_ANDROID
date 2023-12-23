package com.example.api_music_player.repository;

import com.example.api_music_player.model.History;
import com.example.api_music_player.model.Liked;
import com.example.api_music_player.model.Song;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Liked, Long> {
    // Query to get all songs from history for a specific user
    @Query("SELECT l.song FROM Liked l WHERE l.user.id = :userId")
    Page<Song> findAllSongsByUserId(@Param("userId") Long userId, Pageable pageable);


    @Modifying
    @Transactional
    @Query("DELETE FROM Liked l WHERE l.id = :likeId")
    Integer deleteLiked(@Param("likeId") Long likeId);
}
