package com.example.beskbd.dto.object;

import com.example.beskbd.entities.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CategoryDto {
    @JsonProperty
    Long id;
    @JsonProperty
    String categoryName;
    @JsonProperty
    Category.Gender gender;
    @JsonProperty
    String productType;
    @JsonProperty
    String CategoryDescription;

    public CategoryDto(Category category) {
        if (category != null) {
            this.id = category.getId();
            this.categoryName = category.getName();
            this.productType = category.getProductType();
        }
    }


}
