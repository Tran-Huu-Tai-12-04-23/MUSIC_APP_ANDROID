package com.example.api_music_player.repository;

import com.example.api_music_player.model.DetailPlaylist;
import com.example.api_music_player.model.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailPlaylistRepository extends JpaRepository<DetailPlaylist, Long> {

    Page<DetailPlaylist> findAll(Pageable pageable);

    void deleteAllByPlaylistId(Long id);

    List<DetailPlaylist> findAllByPlaylistId(Long playlistId);

    DetailPlaylist findBySongIdAndPlaylistId(long songId, Long playlistId );

}
