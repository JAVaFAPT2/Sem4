package com.example.beskbd.dto.object;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter

public class CartItemDTO {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

    public CartItemDTO(Long productId, String productName, int quantity, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }
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
