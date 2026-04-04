package com.pos.terminal.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String barcode;
    private Double price;
    private Integer quantity;

    // This links the product to a specific Shop Owner
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}