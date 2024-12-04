package com.example.beskbd.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_product_images")
public class ProductImage extends BaseEntity {

    @Column(name = "image_url")
    private String imageUrl; // Store the URL of the image
}