package com.example.beskbd.dto.response;

import com.example.beskbd.entities.Product; // Ensure this import is correct
import lombok.*;

import java.util.Set;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id; // Assuming 'id' is inherited from BaseEntity
    private String name;
    private String description;
    private String gender; // Represent gender as String
    private String productType;
    private Set<Product> products; // Optionally return products
}