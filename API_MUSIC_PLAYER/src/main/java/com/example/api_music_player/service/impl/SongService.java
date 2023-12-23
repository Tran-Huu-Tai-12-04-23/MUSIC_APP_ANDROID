package com.example.api_music_player.service.impl;

import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.repository.LikeRepository;
import com.example.api_music_player.repository.PlaylistRepository;
import com.example.api_music_player.repository.SongRepository;
import com.example.api_music_player.service.ISongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService implements ISongService {
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final LikeRepository likeRepository;

    @Override
    public Song create(Song song) {
        return songRepository.save(song);
    }

    @Override
    public Song update(Song song) {
        return songRepository.findById(song.getId()).map(existingSong -> {
            existingSong.setSongLink(song.getSongLink());
            existingSong.setThumbnails(song.getThumbnails());
            existingSong.setTitle(song.getTitle());
            existingSong.setGenre(song.getGenre());
            return songRepository.save(existingSong);
        }).orElseThrow(() -> new RuntimeException("Sorry, the song could not be found"));
    }

    @Override
    public Song getSongById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sorry, the song could not be found"));
    }

    @Override
    public List<Song> getAll() {
        return songRepository.findAll();
    }

    @Override
    public boolean remove(Long id) {
        if (songRepository.existsById(id)) {
            songRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Song> getAllWithPageSize(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "uploadDate"));
        return songRepository.findAll(pageable).toList();
    }

    @Override
    public List<Song> getNewSong(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadDate"));
        return songRepository.findAll(pageable).toList();
    }

    @Override
    public List<Song> getFamousSong(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "title"));
        return songRepository.findAll(pageable).toList();
    }

    @Override
    public List<Song> getRecentSong(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "songLink"));
        return songRepository.findAll(pageable).toList();
    }

    @Override
    public List<Playlist> getRecentPlaylist(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createAt"));
        return playlistRepository.findAllByIsPrivateFalse(pageable);
    }

    @Override
    public List<Song> search(int page, int size, String key) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadDate"));
        return songRepository.searchSongs(key, pageable).toList();
    }

    @Override
    public List<Song> getSongLikesByUserid(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeDate"));
        return likeRepository.findAllSongsByUserId(userId, pageable).toList();
    }
}
