package com.example.api_music_player.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;


@RequiredArgsConstructor
@Entity
@Data
@Getter
@Setter
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String thumbnails;
    private String songLink;
    private double duration;
    private Date uploadDate;
    private String genre;

    @PrePersist
    public void prePersist() {
        if (uploadDate == null) {
            uploadDate = new Date();
        }
    }

    @ManyToOne
    private User userUpload;
}
