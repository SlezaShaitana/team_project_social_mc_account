package com.social.mc_account.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationDTO {
    private UUID id;
    private UUID authorId;
    private String content;
    private NotificationType notificationType;
    private LocalDateTime sentTime;
    private MicroServiceName serviceName;
    private UUID eventId;
    private Boolean isReaded;
}