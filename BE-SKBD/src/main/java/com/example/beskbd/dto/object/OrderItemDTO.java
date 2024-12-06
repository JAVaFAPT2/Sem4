package com.example.beskbd.dto.object;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private Long quantity;
    private Double price;
}
