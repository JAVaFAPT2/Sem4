package com.example.beskbd.dto.request;

import com.example.beskbd.dto.object.ProductAttributeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
@Getter
@Setter
public class CreateOrderRequest {
    @NotNull
    Long userId;
    @NotBlank
    String address;
    @Size(min = 1, max = 255)
    List<ProductAttributeDto> items;
}
