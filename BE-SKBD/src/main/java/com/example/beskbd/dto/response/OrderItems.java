package com.example.beskbd.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItems {
    private Long productId; // ID of the product
    private String productName; // Name of the product
    private int quantity; // Quantity of the product ordered
    private BigDecimal price; // Price of the product
}