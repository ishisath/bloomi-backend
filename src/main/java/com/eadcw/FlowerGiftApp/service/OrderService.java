package com.eadcw.FlowerGiftApp.service;

import com.eadcw.FlowerGiftApp.entity.*;
import com.eadcw.FlowerGiftApp.repository.*;
import com.eadcw.FlowerGiftApp.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public OrderDTO createOrder(Long userId, CreateOrderRequest request) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new Exception("Order must contain at least one item");
        }

        // Calculate total amount first
        double totalAmount = 0;
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new Exception("Product not found: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new Exception("Insufficient stock for product: " + product.getName());
            }

            totalAmount += product.getPrice() * itemRequest.getQuantity();
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryCity(request.getDeliveryCity());
        order.setDeliveryPostalCode(request.getDeliveryPostalCode());
        order.setNotes(request.getNotes());
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new Exception("Product not found: " + itemRequest.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            orderItemRepository.save(orderItem);

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        savedOrder.setUpdatedAt(System.currentTimeMillis());
        orderRepository.save(savedOrder);

        notificationService.createNotification(
                user,
                "Order Confirmed",
                "Your order #" + savedOrder.getOrderId() + " has been created",
                Notification.NotificationType.ORDER_CONFIRMED
        );

        return convertToDTO(savedOrder);
    }

    public OrderDTO getOrder(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));
        return convertToDTO(order);
    }

    public List<OrderDTO> getUserOrders(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByStatus(Order.OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus newStatus) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        order.setStatus(newStatus);
        order.setUpdatedAt(System.currentTimeMillis());
        Order updatedOrder = orderRepository.save(order);

        String message = "Your order status has been updated to: " + newStatus;
        Notification.NotificationType type = Notification.NotificationType.GENERAL;

        if (newStatus == Order.OrderStatus.SHIPPED) {
            type = Notification.NotificationType.ORDER_SHIPPED;
            message = "Your order has been shipped!";
        } else if (newStatus == Order.OrderStatus.DELIVERED) {
            type = Notification.NotificationType.ORDER_DELIVERED;
            message = "Your order has been delivered!";
        }

        // Create in-app notification
        notificationService.createNotification(order.getUser(), "Order Update", message, type);

        // Send email notification
        notificationService.sendOrderStatusEmail(order.getUser(), order.getOrderId(), newStatus);

        return convertToDTO(updatedOrder);
    }

    public void cancelOrder(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        if (order.getStatus() == Order.OrderStatus.DELIVERED ||
                order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new Exception("Cannot cancel order with status: " + order.getStatus());
        }

        // Return items to stock
        List<OrderItem> orders = orderItemRepository.findByOrder(order);
        for (OrderItem item : orders) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(System.currentTimeMillis());
        orderRepository.save(order);

        notificationService.createNotification(
                order.getUser(),
                "Order Cancelled",
                "Your order #" + orderId + " has been cancelled",
                Notification.NotificationType.GENERAL
        );
    }

    private OrderDTO convertToDTO(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrder(order);

        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getUserId());
        dto.setUserName(order.getUser().getFullName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setDeliveryCity(order.getDeliveryCity());
        dto.setDeliveryPostalCode(order.getDeliveryPostalCode());
        dto.setNotes(order.getNotes());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemDTO> itemDTOs = items.stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setOrderItemId(item.getOrderItemId());
            itemDTO.setProductId(item.getProduct().getProductId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPriceAtPurchase(item.getPriceAtPurchase());
            itemDTO.setTotalPrice(item.getPriceAtPurchase() * item.getQuantity());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}