package com.eadcw.FlowerGiftApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferDTO {
    private Long offerId;
    private Long productId;
    private String productName;
    private String title;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private Double discountPercentage;
    private BigDecimal maxDiscount;
    private BigDecimal minimumOrderValue;
    private Integer maxUsageCount;
    private String startDate;
    private String endDate;
    private Boolean isActive;
    private Long createdAt;
}