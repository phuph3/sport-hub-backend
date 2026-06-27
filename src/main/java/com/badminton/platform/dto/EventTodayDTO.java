package com.badminton.platform.dto;

import com.badminton.platform.entity.EventStatus;
import com.badminton.platform.entity.Sport;

import lombok.Data;

import java.time.LocalDateTime;

import lombok.*;


@Getter
@Setter
@Data

public class EventTodayDTO {

    private Long id;
    private Long hostId;
    private boolean host;

    private String title;
    private String googleMapLink;
    private String locationName;

    private Double lat;
    private Double lng;
    private Long currentPlayers; // ✅ Long (MATCH COUNT)

    private LocalDateTime startTime; // ✅ LocalDateTime
    private LocalDateTime endTime;   // ✅ LocalDateTime

    private EventStatus status; // ✅ enum

    private Integer maxPlayers;

    private String prefectureCode;
    private String cityCode;

    // ✅ ✅ CONSTRUCTOR PHẢI MATCH 100% QUERY
    public EventTodayDTO(
            Long id,
            Long hostId,
            boolean host,
            String title,
            String googleMapLink,
            String locationName,
            Double lat,
            Double lng,
            Long currentPlayers,
            LocalDateTime startTime,
            LocalDateTime endTime,
            EventStatus status,
            Integer maxPlayers,
            String prefectureCode,
            String cityCode
    ) {
        this.id = id;
        this.hostId = hostId;
        this.host = host;
        this.title = title;
        this.googleMapLink = googleMapLink;
        this.locationName = locationName;
        this.lat = lat;
        this.lng = lng;
        this.currentPlayers = currentPlayers;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.prefectureCode = prefectureCode;
        this.cityCode = cityCode;
    }

    // getters/setters (có thể giữ Lombok hoặc viết tay)
}

