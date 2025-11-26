package com.eadcw.FlowerGiftApp.dto;

import com.eadcw.FlowerGiftApp.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private List<OrderItemRequest> items;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryPostalCode;
    private Order.PaymentMethod paymentMethod;
    private String notes;
}
