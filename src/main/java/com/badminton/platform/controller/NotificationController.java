
package com.badminton.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.badminton.platform.entity.Notification;
import com.badminton.platform.entity.UserFollow;
import com.badminton.platform.repository.NotificationRepository;
import com.badminton.platform.service.NotificationService;
import java.util.List;
import com.badminton.platform.dto.NotificationResponseDTO;
import com.badminton.platform.repository.UserFollowRepository;
import com.badminton.platform.service.JwtService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin
public class NotificationController {

    private final NotificationService service;

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtService jwtService;

    // @GetMapping
    // public List<Notification> getNotifications() {

    // Long userId = 1L; // TODO JWT

    // return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
    // }

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // lấy list
    @GetMapping
    public List<NotificationResponseDTO> getNotifications(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtService.getUserIdFromHeader(authHeader);

        return service.getNotifications(userId);
    }

    // badge count

    @GetMapping("/unread-count")
    public long getUnreadCount(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtService.getUserIdFromHeader(authHeader);

        return service.getUnreadCount(userId);
    }



    // mark as read
    @PostMapping("/{id}/read")
    public void markRead(
        @PathVariable Long id,
        @RequestHeader("Authorization") String authHeader) {

    Long userId = jwtService.getUserIdFromHeader(authHeader);

    Notification n = notificationRepo.findById(id).orElse(null);

    if (n != null && n.getUserId().equals(userId)) {
        n.setRead(true);
        notificationRepo.save(n);
    }
}


    // // create global (test)
    // @PostMapping("/global")
    // public Notification createGlobal(@RequestParam String message) {
    // return service.createGlobalNotification(message);
    // }

    @PostMapping("/read-all")
    public void markAllRead(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtService.getUserIdFromHeader(authHeader);

        notificationService.markAllRead(userId);
    }

}