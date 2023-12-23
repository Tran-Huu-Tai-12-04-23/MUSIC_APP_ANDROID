package com.example.api_music_player.service.impl;

import com.example.api_music_player.dto.ChangePasswordRequest;
import com.example.api_music_player.dto.HistoryRequest;
import com.example.api_music_player.exception.PhoneNumberInUseException;
import com.example.api_music_player.exception.UserAlreadyExistsException;
import com.example.api_music_player.model.History;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import com.example.api_music_player.repository.HistoryRepository;
import com.example.api_music_player.repository.SongRepository;
import com.example.api_music_player.repository.UserRepository;
import com.example.api_music_player.service.IHistoryService;
import com.example.api_music_player.service.IUserService;
import com.example.api_music_player.util.Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoryService implements IHistoryService{
    private final HistoryRepository historyRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    @Override
    public Song addHistory(HistoryRequest history) {
        History historyExist = historyRepository.findBySongIdAndUserId(history.getSongId(), history.getUserId());

        System.out.println(history.getSongId());
        if( historyExist != null )
        {
            return null;
        }

        System.out.println(history.getUserId());

        historyExist = new History();
        Optional<Song> songOp = songRepository.findById(history.getSongId());
        Optional<User> userOp = userRepository.findById((long) history.getUserId());


        if( songOp.isEmpty() ) {
            return null;
        }
        if(userOp.isEmpty() ) {
            return null;
        }

        System.out.println("Step 2");


        historyExist.setSong(songOp.get());
        historyExist.setUser(userOp.get());

        History historyNew = historyRepository.save(historyExist);

        return historyNew.getSong();
    }

    @Override
    public List<Song> getHistoryListened(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        return historyRepository.findAllSongsByUserId(userId, pageable).toList();
    }

    @Override
    public Boolean removeHistory(HistoryRequest historyRequest){

        History history = historyRepository.findBySongIdAndUserId(historyRequest.getSongId(), historyRequest.getUserId());

        if( history == null ) {
            return false;
        }
        historyRepository.deleteById((long) history.getId());

        return true;
    }

    @Transactional
    @Override
    public Boolean removeAllHistoryByUserId(int userId) {
        return historyRepository.removeAllByUserId(userId) > 0;

    }
}
