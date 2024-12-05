package com.example.beskbd.services;

import com.example.beskbd.dto.object.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
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
        validateProductCreationRequest(request);
        logger.info("Adding new product: {}", request);

        // Convert ProductCreationRequest to Product entity
        Product product = toProductEntity(request);
        Product savedProduct = productRepository.save(product);
        return toProductDto(savedProduct);
    }

    private Product toProductEntity(ProductCreationRequest request) {
        Product product = new Product();
        product.setName(request.getProductName());
        product.setDescription(request.getProductDescription());

        // Fetch category using categoryId
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        product.setCategory(category);

        // Set product attributes
        List<ProductAttribute> attributes = request.getAttributes()
                .stream()
                .map(this::toProductAttribute)
                .collect(Collectors.toList());
        product.setAttributes(attributes);

        return product;
    }

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
        List<ProductSize> sizes = Optional.ofNullable(dto.getSizes()).orElse(Collections.emptyList())
                .stream()
                .map(this::toProductSize)
                .collect(Collectors.toList());
        productAttribute.setSizes(sizes);

        // Upload product images
        List<ProductImage> productImages = uploadProductImages(dto.getImageFiles());
        productAttribute.setProductImages(productImages);
        productAttribute.setPrice(dto.getPrice());

        return productAttribute;
    }

    private List<ProductImage> uploadProductImages(List<MultipartFile> imageFiles) {
        return imageFiles == null ? Collections.emptyList() : imageFiles.stream()
                .map(imageFile -> {
                    String url = cloudinaryService.uploadImage(imageFile);
                    return new ProductImage(url);
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
                .flatMap(attr -> attr.getProductImages().stream())
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse("");
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
        return toProductDto(product);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductCreationRequest request) {
        logger.info("Updating product ID: {} with request: {}", id, request);

        // Find the existing product by ID
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
        Product updatedProduct = productRepository.save(product);

        // Convert to ProductDto before returning
        return toProductDto(updatedProduct);
    }

    private ProductSizeDto toProductSizeDto(ProductSize productSize) {
        ProductSizeDto dto = new ProductSizeDto();
        dto.setSize(productSize.getSize());
        dto.setStock(productSize.getStock()); // Assuming there's a stock field
        return dto;
    }

//    private ProductAttributeDto toProductAttributeDTO(ProductAttribute productAttribute) {
//        ProductAttributeDto dto = new ProductAttributeDto();
//        dto.setColor(productAttribute.getColor());
//
//        // Map sizes correctly
//        dto.setSizes(productAttribute.getSizes().stream()
//                .map(this::toProductSizeDto)
//                .collect(Collectors.toList()));
//
//        // Convert product images to List<String> instead of List<MultipartFile>
//        dto.setImageFiles(productAttribute.getProductImages()
//                .stream()
//                .map(ProductImage::getImageUrl)
//                .collect(Collectors.toList()));
//
//        dto.setPrice(productAttribute.getPrice());
//
//        return dto;
//    }

    private void validateProductCreationRequest(ProductCreationRequest request) {
        if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        if (request.getCategoryId() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        // Additional validation checks can be added as needed
    }
}