package com.pos.terminal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerEmail;
    private Double totalAmount;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "bill_id")
    private List<BillItem> items;

    // ─── ADD THIS SECTION ───
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // ────────────────────────
}