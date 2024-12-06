package com.example.beskbd.dto.object;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private long categoryId;
    private List<ProductAttributeDto> attributes;
}
