package com.badminton.platform.service;

import com.badminton.platform.entity.Event;
import com.badminton.platform.entity.Notification;
import com.badminton.platform.dto.NotificationResponseDTO;
import com.badminton.platform.repository.EventParticipantRepository;
import com.badminton.platform.repository.EventRepository;

import com.badminton.platform.service.EventService;
import com.badminton.platform.service.JwtService;
import com.badminton.platform.service.NotificationService;
import com.badminton.platform.config.EventWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class JoinEventAsyncService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EventParticipantRepository participantRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EventRepository eventRepository;

    @Async
    public void handleAfterJoin(Event event, Long userId, Long eventId, String status) {
        try {
            // 1. create notification
            Notification n = notificationService.createNotification(
                    event.getHostId(),
                    userId,
                    eventId,
                    "EVENT_JOIN");

            NotificationResponseDTO dto = notificationService.mapToDTO(n);

            Map<String, Object> notiPayload = Map.of(
                    "type", "NOTIFICATION",
                    "userId", event.getHostId(),
                    "notification", dto);

            EventWebSocketHandler.sendToUser(event.getHostId(), notiPayload);

            // 2. update realtime join (broadcast)
            long newCount = participantRepo.countByEventIdAndStatus(eventId, "JOINED");

            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "JOIN");
            msg.put("eventId", eventId);
            msg.put("userId", userId);
            msg.put("status", status);
            msg.put("currentPlayers", newCount);
            msg.put("maxPlayers", event.getMaxPlayers());

            messagingTemplate.convertAndSend("/topic/events", msg);

        } catch (Exception e) {
            // ❗ không throw lại
            System.out.println("Async join error: " + e.getMessage());
        }
    }

    @Async
    public void handleAfterLeave(Long eventId, Long userId, Long promotedUserId) {
        try {

            long newCount = participantRepo.countByEventIdAndStatus(eventId, "JOINED");

            Event event = eventRepository.findById(eventId).orElse(null);

            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "LEAVE");
            msg.put("eventId", eventId);
            msg.put("userId", userId);
            msg.put("status", "LEAVE");
            msg.put("currentPlayers", newCount);
            msg.put("maxPlayers", event != null ? event.getMaxPlayers() : null);

            if (promotedUserId != null) {
                msg.put("promotedUserId", promotedUserId);
            }

            messagingTemplate.convertAndSend("/topic/events", msg);

        } catch (Exception e) {
            System.out.println("Async leave error: " + e.getMessage());
        }
    }
}