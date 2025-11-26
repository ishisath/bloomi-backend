package com.eadcw.FlowerGiftApp.service;



import com.eadcw.FlowerGiftApp.entity.*;
import com.eadcw.FlowerGiftApp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeKey;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationService notificationService;

    public Payment processStripePayment(Long orderId, String stripeToken) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        Stripe.apiKey = stripeKey;

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int)(order.getTotalAmount() * 100)); // Convert to cents
        chargeParams.put("currency", "usd");
        chargeParams.put("source", stripeToken);
        chargeParams.put("description", "Order #" + orderId);

        try {
            Charge charge = Charge.create(chargeParams);

            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(order.getTotalAmount());
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setTransactionId(charge.getId());
            payment.setPaymentDetails("Stripe payment successful");

            Payment savedPayment = paymentRepository.save(payment);

            order.setStatus(Order.OrderStatus.CONFIRMED);
            orderRepository.save(order);

            notificationService.createNotification(
                    order.getUser(),
                    "Payment Successful",
                    "Payment for order #" + orderId + " has been processed",
                    Notification.NotificationType.PAYMENT_SUCCESS
            );

            return savedPayment;
        } catch (Exception e) {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(order.getTotalAmount());
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setPaymentDetails("Error: " + e.getMessage());
            paymentRepository.save(payment);

            throw new Exception("Payment failed: " + e.getMessage());
        }
    }

    public Payment processCashPayment(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentDetails("Cash on delivery");

        Payment savedPayment = paymentRepository.save(payment);

        order.setStatus(Order.OrderStatus.CONFIRMED);
        orderRepository.save(order);

        notificationService.createNotification(
                order.getUser(),
                "Order Confirmed",
                "Your order #" + orderId + " is confirmed. You can pay with cash on delivery",
                Notification.NotificationType.ORDER_CONFIRMED
        );

        return savedPayment;
    }

    public Payment getPayment(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        return paymentRepository.findByOrder(order)
                .orElseThrow(() -> new Exception("Payment not found"));
    }
}
