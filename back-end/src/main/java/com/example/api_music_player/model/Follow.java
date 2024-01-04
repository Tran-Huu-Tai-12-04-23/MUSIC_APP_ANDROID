package com.example.api_music_player.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Data
public class Follow {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "followerId")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followeeId")
    private User followee;
}
