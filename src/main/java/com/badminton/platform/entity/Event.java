package com.badminton.platform.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clubId;
    private Long venueId;

    @Column(name = "google_map_link")
    private String googleMapLink;

    @Column(name = "title")
    private String title;

    @Column(name = "note")
    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @Column(name = "max_players")
    private Integer maxPlayers;

    @Transient
    private Long currentPlayers;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "location_lat")
    private Double locationLat;

    @Column(name = "location_lng")
    private Double locationLng;

    @Column(name = "host_id")
    private Long hostId;

    @ManyToOne
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @Column(name = "prefecture_code")
    private String prefectureCode;

    @Column(name = "city_code")
    private String cityCode;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "level_from")
    private String levelFrom;

    @Column(name = "level_to")
    private String levelTo;

    @Transient
    private Double distance;

    @Column(name = "full_address")
    private String fullAddress;

    // ✅ auto set createdAt
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = EventStatus.OPEN;
    }
}