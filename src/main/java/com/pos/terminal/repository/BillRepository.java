package com.pos.terminal.repository;

import com.pos.terminal.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    // Fetches all bills for a user, newest first
    List<Bill> findByUserIdOrderByCreatedAtDesc(Long userId);
}