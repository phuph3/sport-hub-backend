package com.badminton.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; 

    @Column(name = "actor_id")
    private Long actorId; // nullable = global notification

    @Column(name = "message")
    private String message;

    @Column(name = "notification_type")
    private String type;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "is_global")
    private boolean isGlobal = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //  auto set createdAt
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}