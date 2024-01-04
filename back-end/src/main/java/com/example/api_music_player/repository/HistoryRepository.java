package com.example.api_music_player.repository;

import com.example.api_music_player.model.History;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    // Query to get all songs from history for a specific user
    @Query("SELECT h.song FROM History h WHERE h.user.id = :userId")
    Page<Song> findAllSongsByUserId(@Param("userId") Long userId, Pageable pageable);
    @Modifying
    @Query("DELETE FROM History h WHERE h.id = :historyId")
    int deleteHistory(@Param("historyId") Long historyId);


    History findBySongIdAndUserId(long song_id, int user_id);

    @Modifying
    @Query("DELETE FROM History h WHERE h.user.id = :userId")
    int removeAllByUserId(@Param("userId") int userId);



}
