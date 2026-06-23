package com.badminton.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_follows",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"follower_id", "following_id"})
       })
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "follower_id")
    private Long followerId;

    @Column(name = "following_id")
    private Long followingId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters setters
    public Long getId() { return id; }

    public Long getFollowerId() { return followerId; }
    public void setFollowerId(Long followerId) { this.followerId = followerId; }

    public Long getFollowingId() { return followingId; }
    public void setFollowingId(Long followingId) { this.followingId = followingId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}