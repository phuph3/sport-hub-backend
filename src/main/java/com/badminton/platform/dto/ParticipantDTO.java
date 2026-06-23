package com.badminton.platform.dto;

public class ParticipantDTO {

    private Long userId;
    private String nickname;
    private String fullname;
    private String avatarUrl;

    public ParticipantDTO(Long userId, String nickname, String fullname, String avatarUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.fullname = fullname;
        this.avatarUrl = avatarUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getFullname() {
        return fullname;
    }
}