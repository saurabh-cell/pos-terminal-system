package com.pos.terminal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products") // This creates a table named 'products' in PostgreSQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String barcode; // The key for our barcode scanner

    @Column(nullable = false)
    private String name;

    private Double price;

    private Integer quantity;
}