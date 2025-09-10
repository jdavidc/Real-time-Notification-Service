package com.proxyapi.notificationservice.service;

import com.proxyapi.notificationservice.dto.NotificationDto;
import com.proxyapi.notificationservice.exception.NotificationNotFoundException;
import com.proxyapi.notificationservice.model.Notification;
import com.proxyapi.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public NotificationDto createNotification(NotificationDto notificationDto) {
        Notification notification = Notification.builder()
                .title(notificationDto.getTitle())
                .message(notificationDto.getMessage())
                .recipientId(notificationDto.getRecipientId())
                .type(notificationDto.getType())
                .status(Notification.NotificationStatus.UNREAD)  // Explicitly set status to UNREAD
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        NotificationDto savedDto = NotificationDto.fromEntity(savedNotification);
        
        // Send real-time notification to the recipient
        sendNotificationToUser(notificationDto.getRecipientId(), savedDto);
        
        return savedDto;
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(String userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(String userId, Pageable pageable) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public NotificationDto getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(NotificationDto::fromEntity)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
    }

    @Transactional
    public NotificationDto markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + notificationId));
        
        notification.setStatus(Notification.NotificationStatus.READ);
        return NotificationDto.fromEntity(notificationRepository.save(notification));
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new NotificationNotFoundException("Notification not found with id: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByRecipientIdAndStatus(
                userId, 
                Notification.NotificationStatus.UNREAD
        );
    }

    private void sendNotificationToUser(String userId, NotificationDto notification) {
        String destination = String.format("/topic/notifications/%s", userId);
        messagingTemplate.convertAndSend(destination, notification);
    }
}
