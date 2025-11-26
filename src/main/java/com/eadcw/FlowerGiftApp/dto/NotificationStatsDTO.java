package com.eadcw.FlowerGiftApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationStatsDTO {
    private Long userId;
    private Long unreadCount;
    private Long totalCount;
    private List<NotificationDTO> recentNotifications;
}