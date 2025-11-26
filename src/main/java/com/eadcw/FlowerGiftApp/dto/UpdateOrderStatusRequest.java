package com.eadcw.FlowerGiftApp.dto;

import com.eadcw.FlowerGiftApp.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    private Order.OrderStatus status;
}