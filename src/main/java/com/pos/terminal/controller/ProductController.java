package com.pos.terminal.controller;

import com.pos.terminal.model.Product;
import com.pos.terminal.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173") // Preparing for React (Vite)
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 1. Get all products (to see our inventory)
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 2. Add a new product (The "Manual Entry" before scanning)
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    // 3. Find by Barcode (The "Heart" of your POS scanner)
    @GetMapping("/scan/{barcode}")
    public Product getProductByBarcode(@PathVariable String barcode) {
        return productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Product not found with barcode: " + barcode));
    }
}