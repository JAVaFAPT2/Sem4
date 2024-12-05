package com.example.beskbd.dto.object;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSizeDto {
    @NotNull
    @Min(1)
    private Integer stock;
    @NotNull
    @Min(0)
    private Integer size;

    public ProductSizeDto(Integer stock, Integer size) {
        this.stock = stock;
        this.size = size;
    }
}
