package com.pos.terminal.controller;

import com.pos.terminal.model.Bill;
import com.pos.terminal.repository.BillRepository;
import com.pos.terminal.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "http://localhost:5173")
public class BillController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillService billService;

    // Updated to accept {userId} in the path
    @PostMapping("/generate/{userId}")
    public ResponseEntity<Bill> generateBill(@PathVariable Long userId, @RequestBody Bill bill) {
        // Now passing both required arguments to the service
        Bill savedBill = billService.createBill(bill, userId);
        return ResponseEntity.ok(savedBill);
    }

    @GetMapping("/user/{userId}")
    public List<Bill> getBillsByUser(@PathVariable Long userId) {
        return billRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}