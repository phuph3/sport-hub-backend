package com.badminton.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullname;

    @Column(name = "nickname")
    private String nickname;

    @Column(unique = true)
    private String email;

    @Column(name = "level")
    private String level;

    @JsonIgnore
    @Column(name = "password")
    private String password; 

    @Column(name = "provider")
    private String provider; // GOOGLE / LOCAL

    @Column(name = "providerId")
    private String providerId;

    // contact
    @Column(name = "line_id")
    private String lineId;

    @Column(name = "facebook")
    private String facebook;

    @Column(name = "phone")
    private String phone;

    @Column(name = "avatar_url")
    private String avatarUrl;

    // preference (optional)
    @Column(name = "preferred_area")
    private String preferredArea;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ✅ auto set createdAt
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
