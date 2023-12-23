package com.example.api_music_player.service.impl;

import com.example.api_music_player.model.DetailPlaylist;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.repository.DetailPlaylistRepository;
import com.example.api_music_player.repository.LikeRepository;
import com.example.api_music_player.repository.PlaylistRepository;
import com.example.api_music_player.repository.SongRepository;
import com.example.api_music_player.service.IPlaylistService;
import com.example.api_music_player.service.ISongService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaylistService implements IPlaylistService {

    private final PlaylistRepository playlistRepository;
    private final DetailPlaylistRepository detailPlaylistRepository;

    @Override
    public Playlist create(Playlist playlist) {
        Playlist playlistExistOp = playlistRepository.findByTitle(playlist.getTitle());
        if( playlistExistOp != null ) throw  new RuntimeException("Danh sách phát đã tồn tại!");
        return playlistRepository.save(playlist);
    }

    @Override
    public Playlist update(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    @Transactional
    @Override
    public void removeById(Long playlistId) {
        detailPlaylistRepository.deleteAllByPlaylistId(playlistId);
        playlistRepository.deleteById(playlistId);
    }

    @Override
    public List<Playlist> getAllPlaylistByUserId(int page, int size, int userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        return playlistRepository.findAllByUserId(pageable, userId);
    }

    @Override
    public List<Playlist> getAllPlaylistPrivateByUserId(int page, int size, int userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        return playlistRepository.findAllByUserIdAndIsPrivateTrue(userId, pageable);
    }

    @Override
    public List<Playlist> getAllPlaylistPublicByUserId(int page, int size, int userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        return playlistRepository.findAllByUserIdAndIsPrivateFalse(userId, pageable);
    }

    @Override
    public List<Playlist> getAllPlaylistByUserIdNotHaveSong( int userId, long songId) {
        return playlistRepository.findAllWithoutSongByUserIdAndSongId((long) userId, songId);
    }

    @Override
    public List<DetailPlaylist> getDetailPlaylistById(Long playListId) {
        return detailPlaylistRepository.findAllByPlaylistId(playListId);
    }

    @Override
    public Playlist addSongIntoPlaylist(Long playlistId, long songId) {
        Optional<Playlist> playlistOp = playlistRepository.findById(playlistId);
        if( playlistOp.isEmpty()) throw new RuntimeException("Playlist not found!");
        DetailPlaylist detailPlaylist = detailPlaylistRepository.findBySongIdAndPlaylistId(songId, playlistId);

        if( detailPlaylist != null ) throw  new RuntimeException("Bài hát đã tồn tại trong danh sách phát!");
        detailPlaylist = new DetailPlaylist();
        Song song = new Song();
        song.setId(songId);
        detailPlaylist.setSong(song);
        detailPlaylist.setPlaylist(playlistOp.get());
        detailPlaylistRepository.save(detailPlaylist);
        return playlistOp.get();
    }

    @Override
    public void removeSongPlaylist(int detailPlaylistId) {
        detailPlaylistRepository.deleteById((long) detailPlaylistId);
    }
}
