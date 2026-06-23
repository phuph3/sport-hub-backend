package com.badminton.platform.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    //  constructor đúng
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToUser(Long userId, Object payload) {
        messagingTemplate.convertAndSend(
                "/topic/user/" + userId,
                payload
        );
    }
}