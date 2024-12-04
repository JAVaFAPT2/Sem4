package com.example.beskbd.dto.object;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private Long quantity;
    private Double price;
}
