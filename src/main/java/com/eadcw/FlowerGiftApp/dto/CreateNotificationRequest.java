package com.eadcw.FlowerGiftApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Notification type is required")
    private String notificationType; // ORDER, PAYMENT, OFFER, DELIVERY, GENERAL

    private Long relatedOrderId;
    private Long relatedProductId;
    private Boolean sendEmail;
    private Boolean sendSMS;
}
