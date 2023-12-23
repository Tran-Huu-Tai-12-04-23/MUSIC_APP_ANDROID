package com.example.api_music_player.controller;


import com.example.api_music_player.model.Comment;
import com.example.api_music_player.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/song/comment")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService iCommentService;
    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody Comment commentRequest) {
        System.out.println(commentRequest.getCommentDate());
        return ResponseEntity.ok(iCommentService.create(commentRequest));
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editComment(@RequestBody Comment comment) {
        System.out.println(comment.getContent());
        return ResponseEntity.ok(iCommentService.update(comment));
    }

    @GetMapping("/all/{songId}")
    public ResponseEntity<?> getAllSong(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size,
            @PathVariable long songId
    ) {
        List<Comment> data = iCommentService.getAllCommentBySong(page, size, songId);
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/remove/{commentId}")
    public ResponseEntity<?> removeComment(@PathVariable int commentId ) {
        iCommentService.deleteById(commentId);
        return ResponseEntity.ok(true);
    }

}
