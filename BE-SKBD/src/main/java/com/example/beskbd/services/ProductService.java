package com.example.beskbd.services;

import com.example.beskbd.dto.object.CategoryDto;
import com.example.beskbd.dto.object.NewArrivalProductDto;
import com.example.beskbd.dto.object.ProductAttributeDto;
import com.example.beskbd.dto.object.ProductSizeDto;
import com.example.beskbd.dto.request.ProductCreationRequest;
import com.example.beskbd.dto.response.ProductDto;
import com.example.beskbd.entities.*;
import com.example.beskbd.exception.AppException;
import com.example.beskbd.exception.ErrorCode;
import com.example.beskbd.repositories.CategoryRepository;
import com.example.beskbd.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    public Map<String, List<CategoryDto>> getCategoryByGender() {
        logger.info("Fetching categories by gender");
        return categoryRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(category -> category.getGender().toString(),
                        Collectors.mapping(CategoryDto::new, Collectors.toList())));
    }

    public ProductDto addProduct(ProductCreationRequest request) {
        validateProductCreationRequest(request);  // Check request validity
        logger.info("Adding new product: {}", request);

        // Convert ProductCreationRequest to Product entity
        Product product = toProductEntity(request);
        Product savedProduct = productRepository.save(product);
        return toProductDto(savedProduct);  // Return the DTO of the saved product
    }

    private Product toProductEntity(ProductCreationRequest request) {
        Product product = new Product();
        product.setName(request.getProductName());
        product.setDescription(request.getProductDescription());

        // Fetch category using the categoryId
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        product.setCategory(category);

        // Set product attributes
        List<ProductAttribute> attributes = request.getAttributes()
                .stream()
                .map(this::toProductAttribute)
                .collect(Collectors.toList());
        product.setAttributes(attributes);  // Set attributes for the product

        return product;
    }

    // Convert a Product to its DTO representation
    public ProductDto toProductDto(Product product) {
        return ProductDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productColors(product.getAttributes()
                        .stream()
                        .map(ProductAttribute::getColor)
                        .collect(Collectors.toList()))
                .productImageUrl(getFirstImageUrl(product))
                .productMinPrice(getMinPrice(product))
                .productMaxPrice(getMaxPrice(product))
                .build();
    }

    private ProductAttribute toProductAttribute(ProductAttributeDto dto) {
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setColor(dto.getColor());

        // Ensure sizes is not null
        List<ProductSize> sizes = (dto.getSizes() == null) ? Collections.emptyList() : dto.getSizes()
                .stream()
                .map(this::toProductSize)
                .collect(Collectors.toList());
        productAttribute.setSizes(sizes);

        // Upload product images
        List<ProductImage> productImages = uploadProductImages(dto);
        productAttribute.setProductImages(productImages);
        productAttribute.setPrice(dto.getPrice());

        return productAttribute;
    }


    private List<ProductImage> uploadProductImages(ProductAttributeDto dto) {
        return dto.getImageFiles().stream()
                .map(imageFile -> {
                    String url = cloudinaryService.uploadImage(imageFile);
                    return new ProductImage(url); // Create ProductImage using the URL
                })
                .collect(Collectors.toList());
    }


    private ProductSize toProductSize(ProductSizeDto productSizeDto) {
        return ProductSize.builder()
                .size(productSizeDto.getSize())
                .stock(productSizeDto.getStock())
                .build();
    }

    public List<NewArrivalProductDto> getNewArrivalProduct() {
        logger.info("Fetching new arrival products");
        return productRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(this::toNewArrival)
                .collect(Collectors.toList());
    }

    private NewArrivalProductDto toNewArrival(Product product) {
        return NewArrivalProductDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .minPrice(getMinPrice(product))
                .maxPrice(getMaxPrice(product))
                .imageUrl(getFirstImageUrl(product))
                .build();
    }

    private String getFirstImageUrl(Product product) {
        return product.getAttributes().stream()
                .findFirst()
                .flatMap(attr -> attr.getProductImages().stream().findFirst())
                .map(ProductImage::getImageUrl)
                .orElse(""); // Default to an empty string if no image is available
    }

    private BigDecimal getMaxPrice(Product product) {
        return product.getAttributes()
                .stream()
                .map(ProductAttribute::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getMinPrice(Product product) {
        return product.getAttributes()
                .stream()
                .map(ProductAttribute::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    public List<ProductDto> getAllProducts() {
        logger.info("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(this::toProductDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProductById(Long id) {
        logger.info("Deleting product with ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        productRepository.deleteById(id);
    }

    public ProductDto getProductById(Long id) {
        logger.info("Fetching product by ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return toProductDto(product); // Convert Product to ProductDto before returning
    }

    @Transactional
    public void updateProduct(Long id, ProductCreationRequest request) {
        logger.info("Updating product ID: {} with request: {}", id, request);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Update product fields from the request
        product.setName(request.getProductName());
        product.setDescription(request.getProductDescription());

        // Fetch the category by ID and set it
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        product.setCategory(category);

        // Update existing attributes or replace them as needed
        List<ProductAttribute> attributes = request.getAttributes()
                .stream()
                .map(this::toProductAttribute)
                .collect(Collectors.toList());
        product.setAttributes(attributes);

        // Save the updated product back to the repository
        productRepository.save(product);
    }

    private void validateProductCreationRequest(ProductCreationRequest request) {
        if (request.getProductName() == null || request.getProductName().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        if (request.getCategoryId() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        // More validation checks can be added as needed
    }
}