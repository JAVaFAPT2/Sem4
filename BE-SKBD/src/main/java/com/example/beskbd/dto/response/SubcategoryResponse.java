package com.example.beskbd.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubcategoryResponse {

    private Long id; // Unique identifier for the subcategory
    private String name; // Name of the subcategory
}