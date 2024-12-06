package com.example.beskbd.dto.object;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSizeDto {
    @NotNull
    @Min(1)
    private Integer stock;
    @NotNull
    @Min(0)
    private Integer size;
}
