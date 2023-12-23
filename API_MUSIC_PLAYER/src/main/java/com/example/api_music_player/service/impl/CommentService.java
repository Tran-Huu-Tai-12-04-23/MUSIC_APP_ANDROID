package com.example.api_music_player.service.impl;

import com.example.api_music_player.dto.CommentRequest;
import com.example.api_music_player.model.Comment;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.repository.CommentRepository;
import com.example.api_music_player.repository.LikeRepository;
import com.example.api_music_player.repository.PlaylistRepository;
import com.example.api_music_player.repository.SongRepository;
import com.example.api_music_player.service.ICommentService;
import com.example.api_music_player.service.ISongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;

    @Override
    public Comment create(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment update(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void deleteById(int id) {
        commentRepository.deleteById(Long.valueOf(id));
    }

    @Override
    public List<Comment> getAllCommentBySong(int page, int size, long songId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "commentDate"));
        return commentRepository.findAllBySongId(pageable,songId );
    }
}
