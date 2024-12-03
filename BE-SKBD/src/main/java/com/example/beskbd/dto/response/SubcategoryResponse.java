package com.example.beskbd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryResponse {

    private Long id; // Unique identifier for the subcategory
    private String name; // Name of the subcategory
}