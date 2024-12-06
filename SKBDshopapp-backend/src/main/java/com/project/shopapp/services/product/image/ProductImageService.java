package com.project.shopapp.services.product.image;

import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.ProductImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageService implements IProductImageService {

  private static final Logger logger = LoggerFactory.getLogger(ProductImageService.class);
  private final ProductImageRepository productImageRepository;

  @Override
  @Transactional
  public ProductImage deleteProductImage(Long id) throws DataNotFoundException {
    ProductImage productImage = productImageRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException(
                    String.format("Cannot find product image with id: %d", id)));

    productImageRepository.delete(productImage);

    logger.info("Deleted product image with id: {}", id);
    return productImage;
  }
}