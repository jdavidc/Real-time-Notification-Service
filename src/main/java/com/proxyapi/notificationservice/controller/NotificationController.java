package com.proxyapi.notificationservice.controller;

import com.proxyapi.notificationservice.dto.NotificationDto;
import com.proxyapi.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Notification service is working!");
    }
    
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@RequestParam(required = false) String userId) {
        // For testing, use a default user ID if not provided
        String testUserId = userId != null ? userId : "test-user";
        return ResponseEntity.ok(notificationService.getUserNotifications(testUserId));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(@RequestParam(required = false) String userId) {
        // For testing, use a default user ID if not provided
        String testUserId = userId != null ? userId : "test-user";
        return ResponseEntity.ok(notificationService.getUnreadCount(testUserId));
    }

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@Valid @RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok(notificationService.createNotification(notificationDto));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @MessageMapping("/notifications/send")
    public void sendNotification(@Payload NotificationDto notificationDto, Principal principal) {
        // This handles WebSocket messages sent to /app/notifications/send
        notificationService.createNotification(notificationDto);
    }
}
