package com.eadcw.FlowerGiftApp.dto;

import com.eadcw.FlowerGiftApp.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private String userName;
    private Double totalAmount;
    private Order.OrderStatus status;
    private Order.PaymentMethod paymentMethod;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryPostalCode;
    private String notes;
    private List<OrderItemDTO> items;
    private Long createdAt;
    private Long updatedAt;
}