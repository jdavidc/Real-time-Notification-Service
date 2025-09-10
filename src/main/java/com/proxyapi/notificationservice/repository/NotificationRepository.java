package com.proxyapi.notificationservice.repository;

import com.proxyapi.notificationservice.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
    
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId, Pageable pageable);
    
    List<Notification> findByRecipientIdAndStatus(String recipientId, Notification.NotificationStatus status);
    
    Long countByRecipientIdAndStatus(String recipientId, Notification.NotificationStatus status);
}
