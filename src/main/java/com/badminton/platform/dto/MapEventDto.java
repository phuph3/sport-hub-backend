package com.badminton.platform.dto;

import com.badminton.platform.entity.EventStatus;
import com.badminton.platform.entity.Sport;

import lombok.Data;

import java.time.LocalDateTime;

import lombok.*;


@Getter
@Setter
@Data
public class MapEventDto {

    private Long id;
    private Long hostId;
    private boolean host;

    private String title;
    private String note;

    private String locationName;
    private String googleMapLink;

    private Double lat;
    private Double lng;

    private long currentPlayers;
    private Integer maxPlayers;

    private String startTime;
    private String endTime;

    private String status;
    private String joinStatus;

    private String prefectureCode;
    private String cityCode;

    private Long sportId;
    private String sportName;

    private String levelFrom;
    private String levelTo;

    private boolean favorite;

}
