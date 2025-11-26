package com.eadcw.FlowerGiftApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesDTO {
    private Long productId;
    private String productName;
    private Integer totalQuantitySold;  // ← Note the field name matches setter
    private BigDecimal totalRevenue;     // ← Note the field name matches setter
}