package com.example.api_music_player.service;

import com.example.api_music_player.dto.ChangePasswordRequest;
import com.example.api_music_player.dto.HistoryRequest;
import com.example.api_music_player.model.History;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;

import java.util.List;

public interface IHistoryService {
    Song addHistory(HistoryRequest historyRequest);

    List<Song> getHistoryListened(Long userId, Integer page, Integer size);
    Boolean removeHistory(HistoryRequest historyRequest);
    Boolean removeAllHistoryByUserId(int userId);

}
