package com.example.beskbd.dto.object;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterDto {
    BigDecimal minPrice;
    BigDecimal maxPrice;
    List<String> productSizes;
    List<String> productColors;
    String productType;
    Integer page;
    Integer pageSize;
}
