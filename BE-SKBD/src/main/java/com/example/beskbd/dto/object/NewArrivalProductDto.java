package com.example.beskbd.dto.object;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
@AllArgsConstructor
@Getter
@Setter
public class NewArrivalProductDto {
    @JsonProperty
    public Long productId;
    @JsonProperty
    public String productName;
    @JsonProperty
    public  String categoryName;
    @JsonProperty
    public String imageUrl;
    @JsonProperty
    public BigDecimal minPrice;
    @JsonProperty
    public BigDecimal maxPrice;
    @JsonProperty
    String productDescription;
}
