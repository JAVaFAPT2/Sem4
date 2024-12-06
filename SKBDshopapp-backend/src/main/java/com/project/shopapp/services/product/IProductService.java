package com.project.shopapp.services.product;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.dtos.responses.product.ProductResponse;
import com.project.shopapp.exceptions.DataNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.project.shopapp.models.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {
  Product createProduct(ProductDTO productDTO) throws Exception;

  Product getProductById(long id) throws Exception;

  Page<ProductResponse> getAllProducts(String keyword,
                                       Long categoryId, PageRequest pageRequest);

  Product updateProduct(long id, ProductDTO productDTO) throws Exception;

  void deleteProduct(long id) throws DataNotFoundException;

  boolean existsByName(String name);

  ProductImage createProductImage(
      Long productId,
      ProductImageDTO productImageDTO) throws Exception;

  List<Product> findProductsByIds(List<Long> productIds);

  String storeFile(MultipartFile file) throws IOException;

  void deleteFile(String filename) throws IOException;
}