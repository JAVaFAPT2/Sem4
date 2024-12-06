package com.example.beskbd.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PromotionProductDTO {
    private Long id;
    private Long productId; // Assuming you have a ProductDTO or similar
    private Float percentage;
}
