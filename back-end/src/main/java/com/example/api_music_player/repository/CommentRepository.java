package com.example.api_music_player.repository;

import com.example.api_music_player.model.Comment;
import com.example.api_music_player.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllBySongId(Pageable pageable, long songID);
}
