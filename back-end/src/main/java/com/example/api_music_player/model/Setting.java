package com.example.api_music_player.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Entity
@Data
@Getter
@Setter
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    private boolean autoPlayNext;
    private boolean randomPlay;
    private int soundPower;
    private int showArtistInProfile;
    private boolean hiddenProfile;
    private int qualityDownload;
    private boolean notification;
}
