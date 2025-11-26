package com.eadcw.FlowerGiftApp.service;

import com.eadcw.FlowerGiftApp.entity.*;
import com.eadcw.FlowerGiftApp.repository.*;
import com.eadcw.FlowerGiftApp.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public void createNotification(User user, String title, String message,
                                   Notification.NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }

    public void sendOrderStatusEmail(User user, Long orderId, Order.OrderStatus status) {
        try {
            String statusText = formatOrderStatus(status);
            emailService.sendOrderStatusEmail(
                    user.getEmail(),
                    user.getFullName(),
                    orderId,
                    statusText
            );
        } catch (Exception e) {
            System.err.println("Failed to send order status email: " + e.getMessage());
        }
    }

    private String formatOrderStatus(Order.OrderStatus status) {
        if (status == Order.OrderStatus.SHIPPED) {
            return "SHIPPED";
        } else if (status == Order.OrderStatus.DELIVERED) {
            return "DELIVERED";
        } else if (status == Order.OrderStatus.CONFIRMED) {
            return "CONFIRMED";
        } else if (status == Order.OrderStatus.CANCELLED) {
            return "CANCELLED";
        }
        return status.toString();
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();

        List<Notification> notifications = notificationRepository.findByUserAndIsReadFalse(user);
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public NotificationDTO getNotification(Long notificationId) throws Exception {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exception("Notification not found"));
        return convertToDTO(notification);
    }

    public void markAsRead(Long notificationId) throws Exception {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exception("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalse(user);
            unreadNotifications.forEach(n -> {
                n.setIsRead(true);
                notificationRepository.save(n);
            });
        }
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUser().getUserId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}