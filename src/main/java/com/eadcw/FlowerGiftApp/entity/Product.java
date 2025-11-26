package com.eadcw.FlowerGiftApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stock;

    @Column(length = 100)
    private String category;

    @Column(length = 100)
    private String occasion;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_available", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isAvailable = true;

    @Column(name = "created_at", nullable = false)
    private Long createdAt = System.currentTimeMillis();

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt = System.currentTimeMillis();
}