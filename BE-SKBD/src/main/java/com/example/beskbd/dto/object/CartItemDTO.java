package com.example.beskbd.dto.object;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

    @Override
    public String toString() {
        return "CartItemDTO{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }

}
