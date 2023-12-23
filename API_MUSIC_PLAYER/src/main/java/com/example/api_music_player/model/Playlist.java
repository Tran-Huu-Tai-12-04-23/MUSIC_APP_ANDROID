package com.example.api_music_player.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Data
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String thumbnails;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date createAt;

    private Boolean isPrivate;

    @PrePersist
    public void prePersist() {
        if (createAt == null) {
            createAt = new Date();
        }

        if( thumbnails == null ) {
            thumbnails = "https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1";
        }

        if(isPrivate == null ) {
            isPrivate = false;
        }
    }

    @Formula("(SELECT COUNT(*) FROM detail_playlist dp WHERE dp.playlist_id = id)")
    private Long countSong;

    @ManyToOne
    private User user;
    @Override
    public String toString() {
        return this.getTitle() + this.getThumbnails() + this.getUser().getUsername();
    }

}