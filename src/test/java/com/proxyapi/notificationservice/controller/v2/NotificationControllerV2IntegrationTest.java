package com.proxyapi.notificationservice.controller.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxyapi.notificationservice.TestDataFactory;
import com.proxyapi.notificationservice.dto.NotificationDto;
import com.proxyapi.notificationservice.model.Notification;
import com.proxyapi.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class NotificationControllerV2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification testNotification;
    private NotificationDto testNotificationDto;

    @BeforeEach
    void setUp() {
        testNotification = TestDataFactory.createNotification();
        testNotificationDto = TestDataFactory.createNotificationDto();
        
        // Clear any existing test data
        notificationRepository.deleteAll();
    }

    @Test
    void createNotification_ShouldReturnCreated() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v2/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testNotificationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testNotificationDto.getTitle()))
                .andExpect(jsonPath("$.message").value(testNotificationDto.getMessage()))
                .andExpect(jsonPath("$.recipientId").value(testNotificationDto.getRecipientId()));
    }

    @Test
    void getNotification_WhenExists_ShouldReturnNotification() throws Exception {
        // Arrange
        Notification savedNotification = notificationRepository.save(testNotification);

        // Act & Assert
        mockMvc.perform(get("/api/v2/notifications/{id}", savedNotification.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedNotification.getId()))
                .andExpect(jsonPath("$.title").value(savedNotification.getTitle()));
    }

    @Test
    void getNotification_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v2/notifications/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserNotifications_ShouldReturnPagedResults() throws Exception {
        // Arrange
        Notification savedNotification = notificationRepository.save(testNotification);
        
        // Act & Assert
        mockMvc.perform(get("/api/v2/notifications")
                .param("userId", TestDataFactory.TEST_USER_ID)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(savedNotification.getId()))
                .andExpect(jsonPath("$.content[0].title").value(savedNotification.getTitle()));
    }

    @Test
    void markAsRead_ShouldUpdateStatus() throws Exception {
        // Arrange
        Notification savedNotification = notificationRepository.save(testNotification);
        
        // Act & Assert - First verify it's unread
        mockMvc.perform(patch("/api/v2/notifications/{id}/read", savedNotification.getId()))
                .andExpect(status().isNoContent());
        
        // Verify the status was updated
        Notification updated = notificationRepository.findById(savedNotification.getId()).orElseThrow();
        assertEquals(Notification.NotificationStatus.READ, updated.getStatus());
    }

    @Test
    void deleteNotification_ShouldRemoveNotification() throws Exception {
        // Arrange
        Notification savedNotification = notificationRepository.save(testNotification);
        
        // Act & Assert
        mockMvc.perform(delete("/api/v2/notifications/{id}", savedNotification.getId()))
                .andExpect(status().isNoContent());
        
        // Verify it was deleted
        assertFalse(notificationRepository.existsById(savedNotification.getId()));
    }

    @Test
    void getUnreadCount_ShouldReturnCorrectCount() throws Exception {
        // Arrange
        notificationRepository.save(testNotification); // Unread notification
        
        // Act & Assert
        mockMvc.perform(get("/api/v2/notifications/unread/count")
                .param("userId", TestDataFactory.TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(1));
    }

    @Test
    void createNotification_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        NotificationDto invalidDto = NotificationDto.builder().build(); // Missing required fields
        
        // Act & Assert
        mockMvc.perform(post("/api/v2/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
    }
}
