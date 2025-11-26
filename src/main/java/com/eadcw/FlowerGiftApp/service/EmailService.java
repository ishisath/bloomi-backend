package com.eadcw.FlowerGiftApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderStatusEmail(String toEmail, String customerName, Long orderId, String status) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Order #" + orderId + " - Status Updated");
            message.setText(buildOrderStatusEmailBody(customerName, orderId, status));
            message.setFrom("ishara2468herath@gmail.com");

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    private String buildOrderStatusEmailBody(String customerName, Long orderId, String status) {
        return "Hi " + customerName + ",\n\n" +
                "Your order #" + orderId + " status has been updated to: " + status + "\n\n" +
                "Thank you for shopping with Bloomi!\n\n" +
                "Best regards,\n" +
                "Bloomi Team";
    }
}