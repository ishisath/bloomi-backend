package com.eadcw.FlowerGiftApp.controller;

import com.eadcw.FlowerGiftApp.entity.Payment;
import com.eadcw.FlowerGiftApp.service.PaymentService;
import com.eadcw.FlowerGiftApp.dto.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/stripe")
    public ResponseEntity<?> processStripePayment(@RequestBody PaymentRequest request) {
        try {
            if (request.getOrderId() == null) {
                return ResponseEntity.badRequest().body(createErrorMap("Order ID is required"));
            }
            if (request.getStripeToken() == null || request.getStripeToken().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorMap("Stripe token is required"));
            }

            Payment payment = paymentService.processStripePayment(request.getOrderId(), request.getStripeToken());
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorMap("Payment processing failed: " + e.getMessage()));
        }
    }

    @PostMapping("/cash/{orderId}")
    public ResponseEntity<?> processCashPayment(@PathVariable Long orderId) {
        try {
            if (orderId == null || orderId <= 0) {
                return ResponseEntity.badRequest().body(createErrorMap("Valid order ID is required"));
            }

            Payment payment = paymentService.processCashPayment(orderId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorMap("Payment processing failed: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getPayment(@PathVariable Long orderId) {
        try {
            Payment payment = paymentService.getPayment(orderId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorMap(e.getMessage()));
        }
    }

    private Map<String, String> createErrorMap(String error) {
        Map<String, String> map = new HashMap<>();
        map.put("error", error);
        return map;
    }
}