package com.eadcw.FlowerGiftApp.controller;

import com.eadcw.FlowerGiftApp.entity.Offer;
import com.eadcw.FlowerGiftApp.entity.Product;
import com.eadcw.FlowerGiftApp.service.OfferService;
import com.eadcw.FlowerGiftApp.repository.ProductRepository;
import com.eadcw.FlowerGiftApp.dto.OfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/offers")
@CrossOrigin(origins = "*")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<?> createOffer(@RequestBody Map<String, Object> payload) {
        try {
            // Extract fields from payload
            String offerName = (String) payload.get("offerName");
            String description = (String) payload.get("description");
            Double discountPercentage = null;

            if (payload.get("discountPercentage") != null) {
                discountPercentage = Double.parseDouble(payload.get("discountPercentage").toString());
            }

            String discountType = (String) payload.getOrDefault("discountType", "PERCENTAGE");
            Double discountValue = payload.get("discountValue") != null ?
                    Double.parseDouble(payload.get("discountValue").toString()) : 0.0;
            Double minimumOrderValue = payload.get("minimumOrderValue") != null ?
                    Double.parseDouble(payload.get("minimumOrderValue").toString()) : 0.0;
            String startDate = (String) payload.get("startDate");
            String endDate = (String) payload.get("endDate");
            Boolean isActive = payload.get("isActive") != null ?
                    Boolean.parseBoolean(payload.get("isActive").toString()) : true;

            // Get product ID from nested object
            final Long productId;
            if (payload.get("product") instanceof Map) {
                Map<String, Object> productObj = (Map<String, Object>) payload.get("product");
                productId = Long.parseLong(productObj.get("productId").toString());
            } else if (payload.get("productId") != null) {
                productId = Long.parseLong(payload.get("productId").toString());
            } else {
                return ResponseEntity.badRequest().body(createErrorMap("Product ID is required"));
            }

            // Validate required fields
            if ((offerName == null || offerName.trim().isEmpty())) {
                return ResponseEntity.badRequest().body(createErrorMap("Offer name is required"));
            }

            if (discountPercentage == null || discountPercentage <= 0 || discountPercentage > 100) {
                return ResponseEntity.badRequest()
                        .body(createErrorMap("Discount percentage must be between 0 and 100"));
            }

            if (startDate == null || endDate == null) {
                return ResponseEntity.badRequest().body(createErrorMap("Start and end dates are required"));
            }

            // Get product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

            // Create offer entity
            Offer offer = new Offer();
            offer.setProduct(product);
            offer.setOfferName(offerName);
            offer.setDescription(description);
            offer.setDiscountType(discountType);
            offer.setDiscountPercentage(discountPercentage);
            offer.setDiscountValue(discountValue);
            offer.setMinimumOrderValue(minimumOrderValue);
            offer.setStartDate(startDate);
            offer.setEndDate(endDate);
            offer.setIsActive(isActive);

            Offer createdOffer = offerService.createOffer(offer);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOffer);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorMap("Invalid number format: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorMap("Failed to create offer: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOffers() {
        try {
            List<OfferDTO> offers = offerService.getAllOffers();
            return ResponseEntity.ok(offers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorMap("Failed to fetch offers: " + e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveOffers() {
        try {
            List<OfferDTO> offers = offerService.getActiveOffers();
            return ResponseEntity.ok(offers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorMap("Failed to fetch active offers: " + e.getMessage()));
        }
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<?> getOffer(@PathVariable Long offerId) {
        try {
            OfferDTO offer = offerService.getOfferById(offerId);
            return ResponseEntity.ok(offer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorMap(e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getOfferByProduct(@PathVariable Long productId) {
        try {
            OfferDTO offer = offerService.getOfferByProduct(productId);
            return ResponseEntity.ok(offer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorMap(e.getMessage()));
        }
    }

    @PutMapping("/{offerId}")
    public ResponseEntity<?> updateOffer(@PathVariable Long offerId, @RequestBody Map<String, Object> payload) {
        try {
            Offer updates = new Offer();

            // Extract and set fields from payload
            if (payload.get("offerName") != null) {
                updates.setOfferName((String) payload.get("offerName"));
            }
            if (payload.get("description") != null) {
                updates.setDescription((String) payload.get("description"));
            }
            if (payload.get("discountPercentage") != null) {
                updates.setDiscountPercentage(Double.parseDouble(payload.get("discountPercentage").toString()));
            }
            if (payload.get("discountType") != null) {
                updates.setDiscountType((String) payload.get("discountType"));
            }
            if (payload.get("startDate") != null) {
                updates.setStartDate((String) payload.get("startDate"));
            }
            if (payload.get("endDate") != null) {
                updates.setEndDate((String) payload.get("endDate"));
            }
            if (payload.get("isActive") != null) {
                updates.setIsActive(Boolean.parseBoolean(payload.get("isActive").toString()));
            }

            // Handle product update
            if (payload.get("product") instanceof Map) {
                Map<String, Object> productObj = (Map<String, Object>) payload.get("product");
                Long productId = Long.parseLong(productObj.get("productId").toString());
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                updates.setProduct(product);
            } else if (payload.get("productId") != null) {
                Long productId = Long.parseLong(payload.get("productId").toString());
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                updates.setProduct(product);
            }

            Offer updatedOffer = offerService.updateOffer(offerId, updates);
            return ResponseEntity.ok(updatedOffer);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorMap("Failed to update offer: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<?> deleteOffer(@PathVariable Long offerId) {
        try {
            offerService.deleteOffer(offerId);
            return ResponseEntity.ok(createSuccessMap("Offer deleted successfully"));
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

    private Map<String, String> createSuccessMap(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        return map;
    }
}