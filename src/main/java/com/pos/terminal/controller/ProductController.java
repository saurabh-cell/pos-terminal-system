package com.pos.terminal.controller;

import com.pos.terminal.model.Product;
import com.pos.terminal.model.User;
import com.pos.terminal.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173") // Allow React to connect
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 1. Get products for a specific user
    @GetMapping("/user/{userId}")
    public List<Product> getProductsByUser(@PathVariable Long userId) {
        return productRepository.findByUserId(userId);
    }

    // 2. Add product for a specific user
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> addProduct(@PathVariable Long userId, @RequestBody Product product) {
        // 1. Name check stays the same
        if (productRepository.findByNameAndUserId(product.getName(), userId).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Product with this name already exists!");
        }

        // 2. ONLY check barcode if it's not empty/null
        if (product.getBarcode() != null && !product.getBarcode().trim().isEmpty()) {
            if (productRepository.findByBarcodeAndUserId(product.getBarcode(), userId).isPresent()) {
                return ResponseEntity.badRequest().body("Error: This barcode is already used for another product.");
            }
        }

        User owner = new User();
        owner.setId(userId);
        product.setUser(owner);

        return ResponseEntity.ok(productRepository.save(product));
    }

    // 3. Update a product
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setBarcode(productDetails.getBarcode());
                    product.setPrice(productDetails.getPrice());
                    product.setQuantity(productDetails.getQuantity());
                    productRepository.save(product);
                    return ResponseEntity.ok("Product updated successfully!");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}