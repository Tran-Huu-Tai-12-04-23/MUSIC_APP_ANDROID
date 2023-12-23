package com.example.api_music_player.repository;

import com.example.api_music_player.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SongRepository extends JpaRepository<Song, Long> {


    @Override
    Page<Song> findAll(Pageable pageable);

    Song findByTitle(String title);
    @Query("SELECT s FROM Song s WHERE " +
            "LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.userUpload.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Song> searchSongs(@Param("searchTerm") String searchTerm, Pageable pageable);


}
