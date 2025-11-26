package com.eadcw.FlowerGiftApp.service;

import com.eadcw.FlowerGiftApp.entity.Order;
import com.eadcw.FlowerGiftApp.entity.OrderItem;
import com.eadcw.FlowerGiftApp.repository.OrderRepository;
import com.eadcw.FlowerGiftApp.repository.OrderItemRepository;
import com.eadcw.FlowerGiftApp.repository.ProductRepository;
import com.eadcw.FlowerGiftApp.dto.SalesReportDTO;
import com.eadcw.FlowerGiftApp.dto.ProductSalesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public SalesReportDTO generateSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        SalesReportDTO report = new SalesReportDTO();
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        // Get all orders and filter by date range
        List<Order> allOrders = orderRepository.findAll();

        // Convert LocalDateTime to milliseconds
        long startMs = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endMs = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // Filter orders by date range and status (only confirmed/delivered count as sales)
        List<Order> ordersInRange = allOrders.stream()
                .filter(o -> o.getCreatedAt() >= startMs && o.getCreatedAt() <= endMs)
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED ||
                        o.getStatus() == Order.OrderStatus.CONFIRMED)
                .collect(Collectors.toList());

        long totalOrders = ordersInRange.size();
        double totalRevenue = getTotalSales(ordersInRange);
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        report.setTotalOrders(totalOrders);
        report.setTotalRevenue(BigDecimal.valueOf(totalRevenue).setScale(2, BigDecimal.ROUND_HALF_UP));
        report.setAverageOrderValue(BigDecimal.valueOf(averageOrderValue).setScale(2, BigDecimal.ROUND_HALF_UP));

        // Get top 5 selling products
        List<ProductSalesDTO> topProducts = getTopProducts(ordersInRange, 5);
        report.setTopProducts(topProducts);

        return report;
    }

    private double getTotalSales(List<Order> orders) {
        return orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    private List<ProductSalesDTO> getTopProducts(List<Order> orders, int limit) {
        Map<Long, ProductSalesDTO> productSalesMap = new HashMap<>();

        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrder(order);

            for (OrderItem item : items) {
                Long productId = item.getProduct().getProductId();
                double amount = item.getPriceAtPurchase() * item.getQuantity();

                productSalesMap.computeIfAbsent(productId, k -> {
                    ProductSalesDTO dto = new ProductSalesDTO();
                    dto.setProductId(productId);
                    dto.setProductName(item.getProduct().getName());
                    dto.setTotalQuantitySold(0);
                    dto.setTotalRevenue(BigDecimal.ZERO);
                    return dto;
                });

                ProductSalesDTO dto = productSalesMap.get(productId);
                dto.setTotalQuantitySold(dto.getTotalQuantitySold() + item.getQuantity());
                dto.setTotalRevenue(dto.getTotalRevenue().add(BigDecimal.valueOf(amount)));
            }
        }

        return productSalesMap.values().stream()
                .sorted((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}