package com.example.api_music_player.model;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.Formula;

import java.util.List;

@RequiredArgsConstructor
@Entity
@Data
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;
    private String phoneNumber;
    private String avatar;

    @Formula("(SELECT COUNT(*) FROM follow fl WHERE fl.followee_id = id)")
    private Long totalFollow;

}
