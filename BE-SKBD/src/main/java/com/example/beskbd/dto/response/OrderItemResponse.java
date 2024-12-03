package com.example.beskbd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OrderItemResponse {
    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal price;

}
