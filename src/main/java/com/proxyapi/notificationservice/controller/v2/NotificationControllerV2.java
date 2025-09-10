package com.proxyapi.notificationservice.controller.v2;

import com.proxyapi.notificationservice.dto.NotificationDto;
import com.proxyapi.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Version 2 of the Notification Controller with improved API design.
 * Includes pagination, better error handling, and more RESTful endpoints.
 */
@RestController
@RequestMapping("/api/v2/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications V2", description = "Version 2 of the Notification API")
public class NotificationControllerV2 {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get paginated notifications for a user")
    public ResponseEntity<Page<NotificationDto>> getUserNotifications(
            @RequestParam String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific notification by ID")
    public ResponseEntity<NotificationDto> getNotification(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Get count of unread notifications for a user")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestParam String userId) {
        return ResponseEntity.ok(
            Map.of("unreadCount", notificationService.getUnreadCount(userId))
        );
    }

    @PostMapping
    @Operation(summary = "Create a new notification")
    public ResponseEntity<NotificationDto> createNotification(
            @Valid @RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok(notificationService.createNotification(notificationDto));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
