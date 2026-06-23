package com.badminton.platform.dto;

import com.badminton.platform.entity.EventStatus;

import lombok.Data;

@Data
public class EventResponseDTO {

    private Long id;
    private Long hostId;
    private boolean host;

    private String title;
    private String note;
    private String googleMapLink;
    private String locationName;

    private Double lat;
    private Double lng;
    private Long currentPlayers;

    private String startTime;
    private String endTime;

    private String status;

    private Integer maxPlayers;

    private String prefectureCode;
    private String cityCode;

    private Long sportId;

    private String levelFrom;
    private String levelTo;

    public Long getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(Long currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}