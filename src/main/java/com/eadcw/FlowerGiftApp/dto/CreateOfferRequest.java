package com.eadcw.FlowerGiftApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOfferRequest {
    @NotBlank(message = "Offer name is required")
    private String offerName;

    private String description;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Discount type is required")
    private String discountType; // PERCENTAGE, FIXED_AMOUNT

    @NotNull(message = "Discount value is required")
    @PositiveOrZero(message = "Discount must be non-negative")
    private BigDecimal discountValue;

    private BigDecimal maxDiscount;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @NotNull(message = "Minimum order value is required")
    @PositiveOrZero(message = "Minimum order value must be non-negative")
    private BigDecimal minimumOrderValue;

    private Integer maxUsageCount;
}
