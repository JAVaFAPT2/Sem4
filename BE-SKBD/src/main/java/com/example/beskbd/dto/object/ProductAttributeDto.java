package com.example.beskbd.dto.object;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProductAttributeDto {
    @NotBlank
    private String color;
    @NotNull
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    @NotEmpty
    private List<MultipartFile> imageFiles;
    @NotEmpty
    private List<ProductSizeDto> sizes;

}