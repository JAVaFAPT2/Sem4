package com.example.beskbd.dto.object;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderRequest {
    Long productAttributeId;
    String color;
    Integer quantity;
    Integer size;
    BigDecimal price;
}
