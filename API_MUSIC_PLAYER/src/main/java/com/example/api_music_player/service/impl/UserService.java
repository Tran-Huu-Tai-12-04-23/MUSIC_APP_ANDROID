package com.example.api_music_player.service.impl;

import com.example.api_music_player.model.User;
import com.example.api_music_player.repository.UserRepository;
import com.example.api_music_player.service.IUserService;
import com.example.api_music_player.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    @Override
    public User login(User user) {
        User userLogin = userRepository.findByUsername(user.getUsername().trim());

        if( userLogin == null ) {
            return null;
        }

        if(Util.verifyPassword(user.getPassword().trim(), userLogin.getPassword())){
            return user;
        }
        return null;
    }

    @Override
    public User register(User user) {
        User userExist = userRepository.findByUsername(user.getUsername());

        if( userExist != null ) {
            throw  new RuntimeException("User already exist!");
        }else {
            String hashedPassword = Util.hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
            return userRepository.save(user);
        }
    }

    @Override
    public User changePassword(User user, String newPassword) {
        String hashedPassword = Util.hashPassword(newPassword);
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean deActive(Long id) {
        return false;
    }

    @Override
    public boolean active(Long id) {
        return false;
    }
}
