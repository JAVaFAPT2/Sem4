package com.example.beskbd.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
public class OrderItemResponse {
    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal price;

}
