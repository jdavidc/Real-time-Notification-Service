package com.proxyapi.notificationservice;

import com.proxyapi.notificationservice.dto.NotificationDto;
import com.proxyapi.notificationservice.model.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestDataFactory {

    public static final String TEST_USER_ID = "test-user-123";
    public static final String TEST_TITLE = "Test Notification";
    public static final String TEST_MESSAGE = "This is a test notification message";
    
    public static Notification.NotificationType TEST_TYPE = Notification.NotificationType.INFO;
    
    public static Notification createNotification() {
        return Notification.builder()
                .id(1L)
                .title(TEST_TITLE)
                .message(TEST_MESSAGE)
                .recipientId(TEST_USER_ID)
                .type(TEST_TYPE)
                .status(Notification.NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public static NotificationDto createNotificationDto() {
        return NotificationDto.builder()
                .title(TEST_TITLE)
                .message(TEST_MESSAGE)
                .recipientId(TEST_USER_ID)
                .type(Notification.NotificationType.INFO)
                .build();
    }
    
    public static String asJsonString(final Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
