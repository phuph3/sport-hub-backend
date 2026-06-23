package com.badminton.platform.dto;

import lombok.Data;

@Data
public class UserProfileDTO {

    private Long id;
    private String email;
    private String fullname;
    private String nickname;
    private String level;
    private String avatarUrl;
    private String facebook;
    private String phone;
    private String lineId;
    private String bio;
    private String actorName; 

}