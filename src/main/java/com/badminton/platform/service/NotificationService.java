package com.badminton.platform.service;

import com.badminton.platform.entity.Notification;
import com.badminton.platform.repository.NotificationRepository;
import com.badminton.platform.repository.UserRepository;
import com.badminton.platform.dto.NotificationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.badminton.platform.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private WebSocketService webSocketService;

    // =====================
    // GET LIST
    // =====================
    public List<NotificationResponseDTO> getNotifications(Long userId) {

        List<Notification> list = repo.findAllForUser(userId);

        return list.stream()
                .map(this::mapToDTO)
                .toList();
    }

    // =====================
    // COUNT
    // =====================
    public long getUnreadCount(Long userId) {
        return repo.countUnreadForUser(userId);
    }

    // =====================
    // MARK READ
    // =====================
    @Transactional
    public void markAsRead(Long id) {
        Notification n = repo.findById(id).orElseThrow();
        n.setRead(true);
    }

    // =====================
    // CREATE
    // =====================
    public Notification createNotification(
            Long userId,
            Long actorId,
            Long eventId,
            String type) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setActorId(actorId);
        n.setEventId(eventId);
        n.setType(type);
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());

        return repo.save(n);
    }

    // =====================
    // MAP DTO (QUAN TRỌNG)
    // =====================
    public NotificationResponseDTO mapToDTO(Notification n) {

        String nickname = null;

        if (n.getActorId() != null) {
            User u = userRepo.findById(n.getActorId()).orElse(null);
            nickname = u != null ? u.getNickname() : null;
        }

        System.out.println("MAP DTO >>> id=" + n.getId()
                + ", actorId=" + n.getActorId()
                + ", read=" + n.isRead());

        return NotificationResponseDTO.builder()
                .id(n.getId())
                .type(n.getType())
                .actorId(n.getActorId())
                .actorNickname(nickname)
                .eventId(n.getEventId())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }

    public void markAllRead(Long userId) {
        List<Notification> list = repo.findByUserIdAndIsReadFalse(userId);

        for (Notification n : list) {
            n.setRead(true);
        }

        repo.saveAll(list);
    }

    public void createNotification(
            Long userId,
            Long eventId,
            String type,
            Long actorId,
            String eventTitle) {

        Notification n = new Notification();

        n.setUserId(userId);
        n.setEventId(eventId);
        n.setType(type);
        n.setActorId(actorId);
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(n);

        // PUSH REALTIME
        webSocketService.sendToUser(userId, buildDTO(n, eventTitle));

    }

    private NotificationResponseDTO buildDTO(Notification n, String eventTitle) {

        NotificationResponseDTO dto = new NotificationResponseDTO();

        dto.setId(n.getId());
        dto.setUserId(n.getUserId());
        dto.setEventId(n.getEventId());
        dto.setType(n.getType());
        dto.setActorId(n.getActorId());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());

        //  thêm field custom
        dto.setEventTitle(eventTitle);

        return dto;
    }

}
