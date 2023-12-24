package com.example.api_music_player.service.impl;

import com.example.api_music_player.model.Liked;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.repository.LikeRepository;
import com.example.api_music_player.repository.PlaylistRepository;
import com.example.api_music_player.repository.SongRepository;
import com.example.api_music_player.service.ISongService;
import com.example.api_music_player.util.Util;
import lombok.RequiredArgsConstructor;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.TagException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongService implements ISongService {
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final LikeRepository likeRepository;

    @Override
    public Song create(Song song) throws CannotReadException, TagException, InvalidAudioFrameException, IOException {
        return songRepository.save(song);
    }

    @Override
    public Liked likeSong(Liked liked) {
        Liked lExist = likeRepository.findByUserIdAndSongId(liked.getUser().getId(), liked.getSong().getId());
        if( lExist != null) throw  new RuntimeException("User liked!");
        return likeRepository.save(liked);
    }

    @Override
    public Boolean unLikeSong(Liked liked) {
        Liked likeO = likeRepository.findByUserIdAndSongId(liked.getUser().getId(), liked.getSong().getId());
        if( likeO == null ) throw new RuntimeException("Like not found");
        return likeRepository.deleteLiked((long) likeO.getId()) > 0;
    }

    @Override
    public Boolean isCheckUserLikeSong(Liked liked) {
        Liked lExist = likeRepository.findByUserIdAndSongId(liked.getUser().getId(), liked.getSong().getId());
        return lExist != null;
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
    public Song changeScope(Long songId, Boolean isPrivate) {
        Optional<Song> songOp = songRepository.findById(songId);
        if( songOp.isEmpty() ) {
            throw  new RuntimeException("Song not found!");
        }
        Song song = songOp.get();
        song.setIsPrivate(isPrivate);
        return songRepository.save(song);
    }

    @Override
    public List<Song> getAllWithPageSize(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "uploadDate"));
        return songRepository.findAll(pageable).toList();
    }

    @Override
    public List<Song> getNewSong(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadDate"));
        return songRepository.findAllByIsPrivateFalse(pageable).toList();
    }

    @Override
    public List<Song> getFamousSong(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "title"));
        return songRepository.findAllByIsPrivateFalse(pageable).toList();
    }

    @Override
    public List<Song> getRecentSong(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "songLink"));
        return songRepository.findAllByIsPrivateFalse(pageable).toList();
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

    @Override
    public List<Song> getAllSongByUser(int userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadDate"));
        return songRepository.findAllByUserUploadId(userId, pageable);
    }

    @Override
    public List<Song> getAllSongByUserPrivate(int userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "uploadDate"));
        return songRepository.findAllByUserUploadIdAndIsPrivateTrue(userId, pageable);
    }

    @Override
    public List<Song> getAllSongByUserPublic(int userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "uploadDate"));
        return songRepository.findAllByUserUploadIdAndIsPrivateFalse(userId, pageable);
    }
}
