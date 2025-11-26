package com.eadcw.FlowerGiftApp.repository;

import com.eadcw.FlowerGiftApp.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Query("SELECT o FROM Offer o WHERE o.product.productId = :productId")
    Optional<Offer> findByProduct_ProductId(@Param("productId") Long productId);
}