package com.example.beskbd.entities;

import com.example.beskbd.dto.object.ProductAttributeDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "tbl_product_attributes")
public class ProductAttribute extends BaseEntity {
    @Column(length = 50)
    private String color;
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_attribute_id")
    private List<ProductSize> sizes = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_attribute_id")
    private List<ProductImage> productImages = new ArrayList<>();
}
