package com.badminton.platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.badminton.platform.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private JwtService jwtService;

    private static final List<WebSocketSession> sessions = new ArrayList<>();
    private static final Map<Long, List<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    private Long getUserIdFromToken(WebSocketSession session) {
        try {
            URI uri = session.getUri();

            if (uri == null || uri.getQuery() == null)
                return null;

            String query = uri.getQuery();

            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {

                    String token = param.split("=")[1];

                    return jwtService.extractUserId(token);
                }
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        sessions.add(session);

        Long userId = getUserIdFromToken(session);

        if (userId != null) {

            userSessions.computeIfAbsent(userId, k -> new ArrayList<>())
                    .add(session);

            System.out.println(" WS connected userId=" + userId);
        } else {
            System.out.println("❌ WS missing token");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        sessions.remove(session);

        Long userId = getUserIdFromToken(session);

        List<WebSocketSession> list = userSessions.get(userId);

        if (list != null) {
            list.remove(session); // chỉ xoá session hiện tại

            if (list.isEmpty()) {
                userSessions.remove(userId); // nếu hết session → remove user
            }
        }

    }

    // broadcast tới tất cả client
    public static void broadcast(String message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void broadcast(Object data) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        try {
            String json = mapper.writeValueAsString(data);

            for (WebSocketSession session : sessions) {
                session.sendMessage(new TextMessage(json));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // gửi tin nhắn tới 1 user cụ thể
    public static void sendToUser(Long userId, Object data) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        try {
            String json = mapper.writeValueAsString(data);

            List<WebSocketSession> sessions = userSessions.get(userId);

            if (sessions != null) {
                for (WebSocketSession session : sessions) {
                    try {
                        session.sendMessage(new TextMessage(json));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}