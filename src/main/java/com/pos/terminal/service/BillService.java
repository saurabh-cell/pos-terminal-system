package com.pos.terminal.service;

import com.pos.terminal.model.Bill;
import com.pos.terminal.model.BillItem;
import com.pos.terminal.model.Product;
import com.pos.terminal.model.User;
import com.pos.terminal.repository.BillRepository;
import com.pos.terminal.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender; // New Import
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JavaMailSender mailSender; // Injects your application.properties settings

    @Transactional
    public Bill createBill(Bill bill, Long userId) {
        double total = 0;

        User owner = new User();
        owner.setId(userId);
        bill.setUser(owner);

        StringBuilder itemDetails = new StringBuilder(); // For the email body
        StringBuilder itemRows = new StringBuilder();

        for (BillItem item : bill.getItems()) {
            Product product = productRepository.findByNameAndUserId(item.getProductName(), userId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductName()));

            if (product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName());
            }

            // Update Stock
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            total += product.getPrice() * item.getQuantity();

            // Add to email receipt string
            itemDetails.append(String.format("%s x%d - ₹%.2f\n",
                    item.getProductName(), item.getQuantity(), (item.getPrice() * item.getQuantity())));
            itemRows.append("<tr style='border-bottom: 1px solid #eee;'>")
                    .append("<td style='padding: 10px;'>").append(item.getProductName()).append("</td>")
                    .append("<td style='padding: 10px;'>").append(item.getQuantity()).append("</td>")
                    .append("<td style='padding: 10px;'>₹").append(String.format("%.2f", item.getPrice() * item.getQuantity())).append("</td>")
                    .append("</tr>");
        }

        bill.setTotalAmount(total);
        Bill savedBill = billRepository.save(bill);

        // ─── Trigger the Email ───
        if (bill.getCustomerEmail() != null && !bill.getCustomerEmail().isEmpty()) {
            sendEmailReceipt(bill.getCustomerEmail(), bill.getUser().getShopName(), total, itemRows.toString());
        }

        return savedBill;
    }
    @Async
    private void sendEmailReceipt(String to, String shopName, double total, String itemTableRows) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("saurabh52bansal@gmail.com");
            helper.setTo(to);
            helper.setSubject("Receipt from " + shopName);

            // Build the HTML Body
            String htmlContent =
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; padding: 20px;'>" +
                            "<h2 style='color: #3a6fd8; text-align: center;'>" + shopName + "</h2>" +
                            "<p style='text-align: center; color: #888;'>Digital Tax Invoice</p>" +
                            "<hr style='border: 0; border-top: 1px solid #eee;' />" +
                            "<table style='width: 100%; border-collapse: collapse; margin: 20px 0;'>" +
                            "<thead>" +
                            "<tr style='background: #f8f9fa; text-align: left;'>" +
                            "<th style='padding: 10px; border-bottom: 2px solid #eee;'>Item</th>" +
                            "<th style='padding: 10px; border-bottom: 2px solid #eee;'>Qty</th>" +
                            "<th style='padding: 10px; border-bottom: 2px solid #eee;'>Amount</th>" +
                            "</tr>" +
                            "</thead>" +
                            "<tbody>" + itemTableRows + "</tbody>" +
                            "</table>" +
                            "<div style='text-align: right; font-size: 18px; font-weight: bold; margin-top: 20px;'>" +
                            "Total Amount: <span style='color: #3a6fd8;'>₹" + String.format("%.2f", total) + "</span>" +
                            "</div>" +
                            "<div style='margin-top: 40px; text-align: center; color: #aaa; font-size: 12px;'>" +
                            "Thank you for shopping with us!<br/>This is a computer-generated receipt." +
                            "</div>" +
                            "</div>";

            helper.setText(htmlContent, true); // 'true' enables HTML mode
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("HTML Email failed: " + e.getMessage());
        }
    }
}