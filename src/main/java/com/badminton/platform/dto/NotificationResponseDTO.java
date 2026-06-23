package com.badminton.platform.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {

    private Long id;

    private Long userId;

    private Long actorId;

    private String actorNickname;

    private String message;

    private String type;

    private Long eventId;
    
    private String eventTitle;

    private Boolean read;

    private boolean isGlobal;

    private LocalDateTime createdAt;
}