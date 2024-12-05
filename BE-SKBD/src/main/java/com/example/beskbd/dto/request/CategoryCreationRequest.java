package com.example.beskbd.dto.request;

import com.example.beskbd.entities.Category;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
@Getter
@Setter
@AllArgsConstructor

public class CategoryCreationRequest {

    Long id;
    @NotNull
    String categoryName;
    String categoryDescription;
    @NotNull
    Category.Gender gender;
    @NotNull
    String productType;

    public CategoryCreationRequest(Long id, String name, Category.Gender gender, String description, String productType) {
            this.id = id;
        this.categoryName = name;
        this.gender = gender;
        this.categoryDescription = description;
        this.productType = productType;
    }
}
