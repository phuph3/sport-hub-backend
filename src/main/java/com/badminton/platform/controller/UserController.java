package com.badminton.platform.controller;

import com.badminton.platform.config.EventWebSocketHandler;
import com.badminton.platform.dto.NotificationResponseDTO;
import com.badminton.platform.dto.UserProfileDTO;
import com.badminton.platform.entity.User;
import com.badminton.platform.entity.UserFollow;
import com.badminton.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.badminton.platform.service.*;
import com.badminton.platform.repository.UserFollowRepository;
import com.badminton.platform.entity.Notification;

import com.badminton.platform.entity.ContactMessage;

import com.badminton.platform.repository.ContactRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép Frontend (React) gọi API
public class UserController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserFollowRepository followRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtService jwtService;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    private Long getUserIdFromHeader(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("UNAUTHORIZED");
        }

        String token = authHeader.replace("Bearer ", "");

        return jwtService.extractUserId(token);
    }

    @PostMapping("/contact")
    public Map<String, String> sendContact(
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        String message = body.get("message");

        // validate
        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("Message is empty");
        }

        // tạo entity
        ContactMessage cm = new ContactMessage();
        cm.setUserId(userId);
        cm.setMessage(message);
        cm.setCreatedAt(LocalDateTime.now());

        // save DB
        contactRepository.save(cm);

        return Map.of("status", "ok");
    }

    @GetMapping("/{id}")
    public UserProfileDTO getUserById(@PathVariable Long id) {
        return userService.getProfile(id);
    }

    @PutMapping("/me")
    public UserProfileDTO updateMyProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserProfileDTO dto) {

        Long userId = getUserIdFromHeader(authHeader);

        return userService.updateProfile(userId, dto);
    }

    @PostMapping("/{id}/follow")
    public void follow(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        if (userId.equals(id))
            return;

        if (!followRepo.existsByFollowerIdAndFollowingId(userId, id)) {
            UserFollow f = new UserFollow();
            f.setFollowerId(userId);
            f.setFollowingId(id);
            followRepo.save(f);

            System.out.println("FOLLOW >>> follower=" + userId + " -> following=" + id);

            // CREATE NOTI
            Notification n = notificationService.createNotification(
                    id, // receiver
                    userId, // actor
                    null,
                    "FOLLOW");

            // socket gửi DTO mới
            NotificationResponseDTO dto = notificationService.mapToDTO(n);

            Map<String, Object> payload = Map.of(
                    "type", "NOTIFICATION",
                    "userId", id,
                    "notification", dto);

            EventWebSocketHandler.sendToUser(id, payload);
        }
    }

    @DeleteMapping("/{id}/follow")
    public void unfollow(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        followRepo.deleteByFollowerIdAndFollowingId(userId, id);
    }

    @GetMapping("/{id}/follow-status")
    public boolean followStatus(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        return followRepo.existsByFollowerIdAndFollowingId(userId, id);
    }

    @GetMapping("/{id}/stats")
    public Map<String, Long> getUserStats(@PathVariable Long id) {

        long followers = followRepo.countByFollowingId(id);
        long following = followRepo.countByFollowerId(id);

        return Map.of(
                "followers", followers,
                "following", following);
    }

    @GetMapping("/me/stats")
    public Map<String, Long> getMyStats(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        long followers = followRepo.countByFollowingId(userId);
        long following = followRepo.countByFollowerId(userId);

        return Map.of(
                "followers", followers,
                "following", following);
    }

    @GetMapping("/me")
    public UserProfileDTO getMyProfile(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        return userService.getProfile(userId);
    }

    @GetMapping("/me/following")
    public List<UserProfileDTO> getMyFollowing(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        List<Long> ids = followRepo.findFollowingIds(userId);

        List<User> users = userRepository.findAllById(ids);

        return users.stream().map(u -> {
            UserProfileDTO dto = new UserProfileDTO();
            dto.setId(u.getId());
            dto.setFullname(u.getFullname());
            dto.setNickname(u.getNickname());
            dto.setAvatarUrl(u.getAvatarUrl());
            dto.setLevel(u.getLevel());
            return dto;
        }).toList();
    }

    @GetMapping("/me/followers")
    public List<UserProfileDTO> getMyFollowers(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);

        List<Long> ids = followRepo.findFollowerIds(userId);

        List<User> users = userRepository.findAllById(ids);

        return users.stream().map(u -> {
            UserProfileDTO dto = new UserProfileDTO();
            dto.setId(u.getId());
            dto.setFullname(u.getFullname());
            dto.setNickname(u.getNickname());
            dto.setAvatarUrl(u.getAvatarUrl());
            dto.setLevel(u.getLevel());
            return dto;
        }).toList();
    }

}