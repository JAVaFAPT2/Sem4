package com.example.beskbd.rest;

import com.example.beskbd.dto.object.CategoryDto;
import com.example.beskbd.dto.object.NewArrivalProductDto;
import com.example.beskbd.dto.request.ProductCreationRequest;
import com.example.beskbd.dto.response.ApiResponse;
import com.example.beskbd.dto.response.ProductDto;
import com.example.beskbd.exception.AppException;
import com.example.beskbd.exception.ErrorCode;
import com.example.beskbd.services.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RestProductController {


    static Logger logger = LoggerFactory.getLogger(RestProductController.class);
    @Autowired
    ProductService productService;

    @GetMapping("/by-gender")
    public ApiResponse<Map<String, List<CategoryDto>>> getByGender() {
        logger.info("Fetching categories by gender");
        Map<String, List<CategoryDto>> categories = productService.getCategoryByGender();
        return ApiResponse.<Map<String, List<CategoryDto>>>builder()
                .success(true)
                .data(categories)
                .build();
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductDto> createProduct(@ModelAttribute ProductCreationRequest request) {
        if (request == null) {
            logger.error("Product creation request is null");
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        logger.info("Creating product: {}", request);

        // Ensure to handle product creation properly in service layer
        ProductDto createdProduct = productService.addProduct(request);

        return ApiResponse.<ProductDto>builder()
                .success(true)
                .data(createdProduct)  // Returning the created product DTO
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDto> getProductById(@PathVariable Long id) {
        logger.info("Fetching product by ID: {}", id);
        ProductDto product = productService.getProductById(id);
        return ApiResponse.<ProductDto>builder()
                .success(true)
                .data(product)
                .build();
    }

    @GetMapping("/new-arrivals")
    public ApiResponse<List<NewArrivalProductDto>> getNewArrivals() {
        logger.info("Fetching new arrival products");
        List<NewArrivalProductDto> newArrivals = productService.getNewArrivalProduct();
        return ApiResponse.<List<NewArrivalProductDto>>builder()
                .success(true)
                .data(newArrivals)
                .build();
    }

    @GetMapping("/")
    public ApiResponse<List<ProductDto>> getAllProducts() {
        logger.info("Fetching all products");
        List<ProductDto> products = productService.getAllProducts();
        return ApiResponse.<List<ProductDto>>builder()
                .success(true)
                .data(products)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteProductById(@PathVariable Long id) {
        logger.info("Deleting product by ID: {}", id);
        productService.deleteProductById(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductCreationRequest request) {
        logger.info("Updating product ID: {} with request: {}", id, request);
        productService.updateProduct(id, request);
        return ApiResponse.<ProductDto>builder()
                .success(true)
                .build();
    }
}