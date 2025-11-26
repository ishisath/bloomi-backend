package com.eadcw.FlowerGiftApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offer_id")
    private Long offerId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "offer_name", nullable = false)
    private String offerName;

    @Column(name = "title", nullable = true)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount_percentage", nullable = false)
    private Double discountPercentage;

    @Column(name = "discount_type", nullable = false)
    private String discountType = "PERCENTAGE";

    @Column(name = "discount_value", nullable = false)
    private Double discountValue = 0.0;

    @Column(name = "minimum_order_value", nullable = false)
    private Double minimumOrderValue = 0.0;

    @Column(name = "start_date", nullable = false)
    private String startDate;

    @Column(name = "end_date", nullable = false)
    private String endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private Long createdAt = System.currentTimeMillis();

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt = System.currentTimeMillis();
}