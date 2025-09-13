package com.proxyapi.notificationservice.service;

import com.proxyapi.notificationservice.TestDataFactory;
import com.proxyapi.notificationservice.dto.NotificationDto;
import com.proxyapi.notificationservice.exception.NotificationNotFoundException;
import com.proxyapi.notificationservice.model.Notification;
import com.proxyapi.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;
    private NotificationDto testNotificationDto;

    @BeforeEach
    void setUp() {
        testNotification = TestDataFactory.createNotification();
        testNotificationDto = TestDataFactory.createNotificationDto();
    }

    @Test
    void createNotification_ShouldSaveAndReturnNotification() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act
        NotificationDto result = notificationService.createNotification(testNotificationDto);

        // Assert
        assertNotNull(result);
        assertEquals(testNotificationDto.getTitle(), result.getTitle());
        assertEquals(testNotificationDto.getMessage(), result.getMessage());
        assertEquals(testNotificationDto.getRecipientId(), result.getRecipientId());
        
        // Verify WebSocket message was sent
        verify(messagingTemplate).convertAndSend(
            eq("/topic/notifications/" + testNotificationDto.getRecipientId()),
            any(NotificationDto.class)
        );
    }

    @Test
    void getUserNotifications_ShouldReturnPagedNotifications() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Notification> notificationPage = new PageImpl<>(List.of(testNotification), pageable, 1);
        
        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(
            anyString(), 
            any(Pageable.class)
        )).thenReturn(notificationPage);

        // Act
        var result = notificationService.getUserNotifications(
            TestDataFactory.TEST_USER_ID, 
            pageable
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(TestDataFactory.TEST_TITLE, result.getContent().get(0).getTitle());
    }

    @Test
    void getNotificationById_WhenExists_ShouldReturnNotification() {
        // Arrange
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        // Act
        NotificationDto result = notificationService.getNotificationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testNotification.getTitle(), result.getTitle());
    }

    @Test
    void getNotificationById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotificationNotFoundException.class, () -> 
            notificationService.getNotificationById(999L)
        );
    }

    @Test
    void markAsRead_ShouldUpdateNotificationStatus() {
        // Arrange
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        NotificationDto result = notificationService.markAsRead(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Notification.NotificationStatus.READ, testNotification.getStatus());
        verify(notificationRepository).save(testNotification);
    }

    @Test
    void deleteNotification_WhenExists_ShouldDelete() {
        // Arrange
        when(notificationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(1L);

        // Act
        notificationService.deleteNotification(1L);

        // Assert
        verify(notificationRepository).deleteById(1L);
    }

    @Test
    void deleteNotification_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(notificationRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(NotificationNotFoundException.class, () -> 
            notificationService.deleteNotification(999L)
        );
        verify(notificationRepository, never()).deleteById(anyLong());
    }

    @Test
    void getUnreadCount_ShouldReturnCorrectCount() {
        // Arrange
        long expectedCount = 5L;
        when(notificationRepository.countByRecipientIdAndStatus(
            TestDataFactory.TEST_USER_ID, 
            Notification.NotificationStatus.UNREAD
        )).thenReturn(expectedCount);

        // Act
        long result = notificationService.getUnreadCount(TestDataFactory.TEST_USER_ID);

        // Assert
        assertEquals(expectedCount, result);
    }
}
