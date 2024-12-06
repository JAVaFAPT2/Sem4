package com.project.shopapp.services.product;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.dtos.responses.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
  private static final String UPLOADS_FOLDER = "uploads";
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductImageRepository productImageRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Override
  @Transactional
  public Product createProduct(ProductDTO productDTO) throws DataNotFoundException, InvalidParamException {
    validateProductDTO(productDTO);

    Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
            .orElseThrow(() -> new DataNotFoundException("Cannot find category with id: " + productDTO.getCategoryId()));

    Product newProduct = Product.builder()
            .name(productDTO.getName())
            .price(productDTO.getPrice())
            .thumbnail(productDTO.getThumbnail())
            .description(productDTO.getDescription())
            .category(existingCategory)
            .build();
    return productRepository.save(newProduct);
  }

  @Override
  public Product getProductById(long productId) throws DataNotFoundException {
    return productRepository.findById(productId)
            .orElseThrow(() -> new DataNotFoundException("Cannot find product with id =" + productId));
  }

  @Override
  public List<Product> findProductsByIds(List<Long> productIds) {
    return productRepository.findProductsByIds(productIds);
  }

  @Override
  public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
    Page<Product> productsPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
    return productsPage.map(ProductResponse::fromProduct);
  }

  @Override
  @Transactional
  public Product updateProduct(long id, ProductDTO productDTO) throws DataNotFoundException, InvalidParamException {
    validateProductDTO(productDTO);

    Product existingProduct = getProductById(id);
    Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
            .orElseThrow(() -> new DataNotFoundException("Cannot find category with id: " + productDTO.getCategoryId()));

    if (productDTO.getName() != null && !productDTO.getName().isEmpty()) {
      existingProduct.setName(productDTO.getName());
    }
    existingProduct.setCategory(existingCategory);
    if (productDTO.getPrice() >= 0) {
      existingProduct.setPrice(productDTO.getPrice());
    }
    if (productDTO.getDescription() != null && !productDTO.getDescription().isEmpty()) {
      existingProduct.setDescription(productDTO.getDescription());
    }
    if (productDTO.getThumbnail() != null && !productDTO.getThumbnail().isEmpty()) {
      existingProduct.setThumbnail(productDTO.getThumbnail()); // Fixed from setDescription
    }

    return productRepository.save(existingProduct);
  }

  @Override
  @Transactional
  public void deleteProduct(long id) throws DataNotFoundException {
    Product existingProduct = getProductById(id);
    productRepository.delete(existingProduct);
  }

  @Override
  public boolean existsByName(String name) {
    return productRepository.existsByName(name);
  }

  @Override
  @Transactional
  public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException {
    Product existingProduct = productRepository.findById(productId)
            .orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + productImageDTO.getProductId()));

    // Check the number of images
    if (productImageRepository.findByProductId(productId).size() >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
      throw new InvalidParamException("Number of images must be <= " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
    }

    ProductImage newProductImage = ProductImage.builder()
            .product(existingProduct)
            .imageUrl(productImageDTO.getImageUrl())
            .build();

    // Set the thumbnail if none is set
    if (existingProduct.getThumbnail() == null) {
      existingProduct.setThumbnail(newProductImage.getImageUrl());
    }

    productRepository.save(existingProduct); // Save the updated product
    return productImageRepository.save(newProductImage);
  }

  @Override
  public void deleteFile(String filename) throws IOException {
    java.nio.file.Path uploadDir = Paths.get(UPLOADS_FOLDER);
    java.nio.file.Path filePath = uploadDir.resolve(filename);

    if (Files.exists(filePath)) {
      Files.delete(filePath);
    } else {
      throw new FileNotFoundException("File not found: " + filename);
    }
  }

  private boolean isImageFile(MultipartFile file) {
    String contentType = file.getContentType();
    return contentType != null && contentType.startsWith("image/");
  }

  @Override
  public String storeFile(MultipartFile file) throws IOException {
    if (!isImageFile(file) || file.getOriginalFilename() == null) {
      throw new IOException("Invalid image format");
    }

    String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    String uniqueFilename = UUID.randomUUID() + "_" + filename;
    java.nio.file.Path uploadDir = Paths.get(UPLOADS_FOLDER);

    if (!Files.exists(uploadDir)) {
      Files.createDirectories(uploadDir);
    }

    java.nio.file.Path destination = uploadDir.resolve(uniqueFilename);
    Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
    return uniqueFilename;
  }

  private void validateProductDTO(ProductDTO productDTO) throws InvalidParamException {
    // Add additional validation logic based on your needs
    if (productDTO.getCategoryId() == null) {
      throw new InvalidParamException("Category ID cannot be null");
    }
    if (productDTO.getName() == null || productDTO.getName().isEmpty()) {
      throw new InvalidParamException("Product name cannot be null or empty");
    }
  }
}

