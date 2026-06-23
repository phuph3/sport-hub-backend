package com.badminton.platform.service;

import com.badminton.platform.dto.UserProfileDTO;
import com.badminton.platform.entity.User;
import com.badminton.platform.repository.UserRepository;
import org.springframework.stereotype.Service;



@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
    }

    /**
     * find or create user when login (Google, etc.)
     */
    public User findOrCreate(String email, String name, String avatar) {

        return userRepository.findByEmail(email)

                .map(user -> {
                    // update new info when login
                    user.setFullname(name);
                    if (avatar != null && !avatar.isEmpty()) {
                        user.setAvatarUrl(avatar);
                    }
                    return userRepository.save(user);
                })

                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    user.setFullname(name);
                    user.setAvatarUrl(avatar);
                    return userRepository.save(user);
                });
    }

    /**
     * get profile
     */
    public UserProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToDTO(user);
    }

    /**
     * update profile (partial update)
     */
    public UserProfileDTO updateProfile(Long userId, UserProfileDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // chỉ update nếu có value
        if (dto.getFullname() != null) {
            user.setFullname(dto.getFullname().trim());
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname().trim());
        }

        if (dto.getLevel() != null) {
            user.setLevel(dto.getLevel());
        }

        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }

        // contact methods (optional)
        if (dto.getLineId() != null) {
            user.setLineId(dto.getLineId());
        }

        if (dto.getFacebook() != null) {
            user.setFacebook(dto.getFacebook());
        }

        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }

        userRepository.save(user);

        return mapToDTO(user);
    }

    /**
     * entity -> dto
     */
    private UserProfileDTO mapToDTO(User u) {
        UserProfileDTO dto = new UserProfileDTO();

        dto.setId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setFullname(u.getFullname());
        dto.setNickname(u.getNickname());
        dto.setLevel(u.getLevel());
        dto.setAvatarUrl(u.getAvatarUrl());
        dto.setLineId(u.getLineId());
        dto.setFacebook(u.getFacebook());
        dto.setPhone(u.getPhone());

        return dto;
    }
}