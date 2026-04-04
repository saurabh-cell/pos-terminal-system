package com.pos.terminal.repository;

import com.pos.terminal.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Get all products belonging to one specific user
    List<Product> findByUserId(Long userId);

    // Add this line back to the interface
    Optional<Product> findByName(String name);

    // Check if a product name exists ONLY for this specific user
    Optional<Product> findByNameAndUserId(String name, Long userId);
    Optional<Product> findByBarcodeAndUserId(String barcode, Long userId);
}