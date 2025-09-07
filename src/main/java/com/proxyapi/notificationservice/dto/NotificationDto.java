package com.proxyapi.notificationservice.dto;

import com.proxyapi.notificationservice.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private String recipientId;
    private Notification.NotificationStatus status;
    private Notification.NotificationType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NotificationDto fromEntity(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .recipientId(notification.getRecipientId())
                .status(notification.getStatus())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
