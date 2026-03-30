package com.pos.terminal.repository;

import com.pos.terminal.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query to find product by barcode (Crucial for the POS logic)
    Optional<Product> findByBarcode(String barcode);
}