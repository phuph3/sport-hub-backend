package com.badminton.platform.dto;

import lombok.Data;

@Data
public class EventRequestDTO {

    private String title; // FE gửi text (1 ngôn ngữ)
    private String note;

    private String googleMapLink;
    private String locationName;

    private Double lat;
    private Double lng;

    private String startTime;
    private String endTime;

    private Integer maxPlayers;

    private String prefectureCode;
    private String cityCode;

    private Long sportId;

    private String levelFrom;
    private String levelTo;

    private String fullAddress;
}