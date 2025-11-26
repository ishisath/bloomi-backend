package com.eadcw.FlowerGiftApp.service;

import com.eadcw.FlowerGiftApp.entity.Offer;
import com.eadcw.FlowerGiftApp.entity.Product;
import com.eadcw.FlowerGiftApp.repository.OfferRepository;
import com.eadcw.FlowerGiftApp.repository.ProductRepository;
import com.eadcw.FlowerGiftApp.dto.OfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ProductRepository productRepository;

    public Offer createOffer(Offer offer) {
        // Validate product exists
        if (offer.getProduct() == null || offer.getProduct().getProductId() == null) {
            throw new IllegalArgumentException("Product is required");
        }

        Product product = productRepository.findById(offer.getProduct().getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        offer.setProduct(product);

        // Set offerName if not provided
        if (offer.getOfferName() == null || offer.getOfferName().isEmpty()) {
            offer.setOfferName(offer.getTitle() != null ? offer.getTitle() : "Offer");
        }

        offer.setCreatedAt(System.currentTimeMillis());
        offer.setUpdatedAt(System.currentTimeMillis());
        return offerRepository.save(offer);
    }

    public List<OfferDTO> getAllOffers() {
        return offerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OfferDTO> getActiveOffers() {
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        return offerRepository.findAll()
                .stream()
                .filter(o -> !(o.getStartDate().compareTo(todayStr) > 0) && !(o.getEndDate().compareTo(todayStr) < 0))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OfferDTO getOfferById(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
        return convertToDTO(offer);
    }

    public OfferDTO getOfferByProduct(Long productId) {
        Offer offer = offerRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new RuntimeException("No offer found for this product"));
        return convertToDTO(offer);
    }

    public Offer updateOffer(Long offerId, Offer updates) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (updates.getProduct() != null && updates.getProduct().getProductId() != null) {
            Product product = productRepository.findById(updates.getProduct().getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            offer.setProduct(product);
        }

        if (updates.getOfferName() != null && !updates.getOfferName().isEmpty()) {
            offer.setOfferName(updates.getOfferName());
        }
        if (updates.getTitle() != null) {
            offer.setTitle(updates.getTitle());
        }
        if (updates.getDescription() != null) {
            offer.setDescription(updates.getDescription());
        }
        if (updates.getDiscountPercentage() != null) {
            offer.setDiscountPercentage(updates.getDiscountPercentage());
        }
        if (updates.getDiscountType() != null) {
            offer.setDiscountType(updates.getDiscountType());
        }
        if (updates.getDiscountValue() != null) {
            offer.setDiscountValue(updates.getDiscountValue());
        }
        if (updates.getMinimumOrderValue() != null) {
            offer.setMinimumOrderValue(updates.getMinimumOrderValue());
        }
        if (updates.getStartDate() != null) {
            offer.setStartDate(updates.getStartDate());
        }
        if (updates.getEndDate() != null) {
            offer.setEndDate(updates.getEndDate());
        }
        if (updates.getIsActive() != null) {
            offer.setIsActive(updates.getIsActive());
        }

        offer.setUpdatedAt(System.currentTimeMillis());
        return offerRepository.save(offer);
    }

    public void deleteOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
        offerRepository.delete(offer);
    }

    private OfferDTO convertToDTO(Offer offer) {
        OfferDTO dto = new OfferDTO();
        dto.setOfferId(offer.getOfferId());
        dto.setProductId(offer.getProduct().getProductId());
        dto.setProductName(offer.getProduct().getName());
        dto.setTitle(offer.getOfferName() != null ? offer.getOfferName() : offer.getTitle());
        dto.setDescription(offer.getDescription());
        dto.setDiscountType(offer.getDiscountType());
        dto.setDiscountValue(BigDecimal.valueOf(offer.getDiscountValue()));
        dto.setDiscountPercentage(offer.getDiscountPercentage());
        dto.setMinimumOrderValue(BigDecimal.valueOf(offer.getMinimumOrderValue()));
        dto.setStartDate(offer.getStartDate());
        dto.setEndDate(offer.getEndDate());
        dto.setIsActive(isOfferActive(offer));
        dto.setCreatedAt(offer.getCreatedAt());
        return dto;
    }

    private boolean isOfferActive(Offer offer) {
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();
        return offer.getStartDate().compareTo(todayStr) <= 0 && offer.getEndDate().compareTo(todayStr) >= 0;
    }
}