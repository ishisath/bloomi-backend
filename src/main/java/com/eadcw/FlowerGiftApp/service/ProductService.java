package com.eadcw.FlowerGiftApp.service;

import com.eadcw.FlowerGiftApp.entity.Product;
import com.eadcw.FlowerGiftApp.entity.Offer;
import com.eadcw.FlowerGiftApp.repository.ProductRepository;
import com.eadcw.FlowerGiftApp.repository.OfferRepository;
import com.eadcw.FlowerGiftApp.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OfferRepository offerRepository;

    public Product createProduct(Product product) {
        product.setCreatedAt(System.currentTimeMillis());
        product.setUpdatedAt(System.currentTimeMillis());
        return productRepository.save(product);
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getAvailableProducts() {
        List<Product> products = productRepository.findByIsAvailableTrue();
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> searchProducts(String keyword) {
        List<Product> products = productRepository.searchByKeyword(keyword);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByOccasion(String occasion) {
        List<Product> products = productRepository.findByOccasion(occasion);
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long productId) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));
        return convertToDTO(product);
    }

    public Product updateProduct(Long productId, Product updates) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        if (updates.getName() != null) product.setName(updates.getName());
        if (updates.getDescription() != null) product.setDescription(updates.getDescription());
        if (updates.getPrice() != null) product.setPrice(updates.getPrice());
        if (updates.getStock() != null) product.setStock(updates.getStock());
        if (updates.getCategory() != null) product.setCategory(updates.getCategory());
        if (updates.getOccasion() != null) product.setOccasion(updates.getOccasion());
        if (updates.getImageUrl() != null) product.setImageUrl(updates.getImageUrl());
        if (updates.getIsAvailable() != null) product.setIsAvailable(updates.getIsAvailable());

        product.setUpdatedAt(System.currentTimeMillis());
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));
        productRepository.delete(product);
    }

    public void updateStock(Long productId, Integer quantity) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategory(product.getCategory());
        dto.setOccasion(product.getOccasion());
        dto.setImageUrl(product.getImageUrl());
        dto.setIsAvailable(product.getIsAvailable());

        // Check for active offers using correct repository method
        Optional<Offer> offer = offerRepository.findByProduct_ProductId(product.getProductId());
        if (offer.isPresent() && offer.get().getIsActive()) {
            Offer o = offer.get();

            // Parse dates as strings and compare with today
            LocalDate today = LocalDate.now();
            LocalDate startDate = LocalDate.parse(o.getStartDate());
            LocalDate endDate = LocalDate.parse(o.getEndDate());

            // Check if offer is currently active (today is between start and end date)
            if (!today.isBefore(startDate) && !today.isAfter(endDate)) {
                double discountAmount = product.getPrice() * (o.getDiscountPercentage() / 100);
                dto.setDiscountedPrice(product.getPrice() - discountAmount);
                dto.setDiscountPercentage(o.getDiscountPercentage());
            }
        }

        return dto;
    }
}