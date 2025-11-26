package com.eadcw.FlowerGiftApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    private String occasion;
    private String imageUrl;
    private Boolean isAvailable;
    private Double discountedPrice;
    private Double discountPercentage;
}
