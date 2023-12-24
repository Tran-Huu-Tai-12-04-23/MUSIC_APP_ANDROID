package com.example.api_music_player.service.impl;

import com.example.api_music_player.dto.ChangePasswordRequest;
import com.example.api_music_player.dto.UserChangePasswordRequest;
import com.example.api_music_player.exception.PhoneNumberInUseException;
import com.example.api_music_player.exception.UserAlreadyExistsException;
import com.example.api_music_player.model.Profile;
import com.example.api_music_player.model.User;
import com.example.api_music_player.repository.ProfileRepository;
import com.example.api_music_player.repository.UserRepository;
import com.example.api_music_player.service.IUserService;
import com.example.api_music_player.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    @Override
    public User login(User user) {
        User userLogin = userRepository.findByUsername(user.getUsername().trim());

        if( userLogin == null ) {
            return null;
        }

        if(Util.verifyPassword(user.getPassword().trim(), userLogin.getPassword())){
            return userLogin;
        }else {
            throw new RuntimeException("Mật khẩu không đúng!");
        }
    }

    @Override
    public User register(User user) {
        User userExist = userRepository.findByUsername(user.getUsername());
        User userExistPhone = userRepository.findByPhoneNumber(user.getPhoneNumber());

        if (userExist != null) {
            throw new UserAlreadyExistsException("Tên tài khoản đã tồn tại!");
        } else if (userExistPhone != null) {
            throw new PhoneNumberInUseException("Số điện thoại đã tồn tại!");
        } else {
            String hashedPassword = Util.hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
            return userRepository.save(user);
        }
    }

    @Override
    public User changePassword(ChangePasswordRequest changePasswordRequest) {
        User user  = userRepository.findByPhoneNumber(changePasswordRequest.getUserPhone());
        if( user== null ) {
            throw  new RuntimeException("No user with  " + changePasswordRequest.getUserPhone() +"!");
        }
        if( !changePasswordRequest.getPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new RuntimeException("Confirm password not match!");
        }

        String hashedPassword = Util.hashPassword(changePasswordRequest.getPassword());
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

    @Override
    public List<User> getFamousArtist(int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.max(size, 1),Sort.by(Sort.Direction.DESC, "totalFollow"));
        List<User> users = userRepository.findAllByTotalFollowNotNull(  pageable).stream().toList();
        return users;
    }

    @Override
    public User changePasswordByUser(UserChangePasswordRequest userChangePasswordRequest) {
        if(! userChangePasswordRequest.getPassword().equals(userChangePasswordRequest.getConfirmPassword()))
            throw  new RuntimeException("Xác nhận mật khẩu mới không khớp với nhau!");

        Optional<User> userOp = userRepository.findById((long) userChangePasswordRequest.getUserId());

        if( userOp.isEmpty() ) throw new RuntimeException("Không tìm thấy tài khoản!");

        User user = userOp.get();
        if(!Util.verifyPassword(userChangePasswordRequest.getOldPassword().trim(), user.getPassword())){
            throw new RuntimeException("Mật khẩu không đúng!");
        }

        user.setPassword(Util.hashPassword(userChangePasswordRequest.getPassword()));
        user.setId(userChangePasswordRequest.getUserId());

        return userRepository.save(user);
    }

    @Override
    public Profile editProfile(Profile profile) {

        User user = userRepository.findByUsername(profile.getUser().getUsername());
        if( user == null ) throw  new RuntimeException("User not found");
        user.setAvatar(profile.getUser().getAvatar());
        userRepository.save(user);

        Profile profileExit = profileRepository.findByUserId(profile.getUser().getId());

        if( profileExit == null ) {
            return profileRepository.save(profile);
        }

        profile.setId(profileExit.getId());

        return profileRepository.save(profile);
    }

    @Override
    public Profile getProfileByUser(int userId) {
        return profileRepository.findByUserId(userId);
    }
}
