package com.example.beskbd.dto.request;

import com.example.beskbd.dto.object.CategoryDto;
import com.example.beskbd.dto.object.ProductAttributeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreationRequest {
    @NotBlank
    @Size(max = 100)
    private String productName;
    @Size(max = 1000)
    private String productDescription;
    @NotEmpty
    private List<ProductAttributeDto> attributes;
    private CategoryDto categoryName;
    @NotNull
    private Long categoryId;
}
