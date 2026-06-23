
package com.badminton.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_events",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
public class FavoriteEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long eventId;

    private LocalDateTime createdAt = LocalDateTime.now();

    //  getter setter
    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
