package com.example.api_music_player.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Date;
@RequiredArgsConstructor
@Entity
@Data
@Getter
@Setter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    private String firstName;
    private String lastName;
    private String avatar;
    private String region;
    private Date dateOfBirth;
}
