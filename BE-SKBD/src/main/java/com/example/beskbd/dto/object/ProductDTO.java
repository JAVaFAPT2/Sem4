package com.example.beskbd.dto.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
}
