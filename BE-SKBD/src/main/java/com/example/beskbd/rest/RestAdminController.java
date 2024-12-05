package com.example.beskbd.rest;


import com.example.beskbd.dto.object.UserDTO;
import com.example.beskbd.dto.request.UserCreationRequest;
import com.example.beskbd.dto.request.ProductCreationRequest;
import com.example.beskbd.dto.request.CategoryCreationRequest;
import com.example.beskbd.dto.response.ApiResponse;
import com.example.beskbd.dto.response.AuthenticationResponse;
import com.example.beskbd.dto.response.ProductDto;
import com.example.beskbd.dto.response.OrderResponse;
import com.example.beskbd.dto.response.CategoryResponse;
import com.example.beskbd.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mysql.cj.conf.PropertyKey.logger;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class RestAdminController {

    private final AdminService adminService;
    private final Logger logger = LoggerFactory.getLogger(RestAdminController.class);

    // Get all users
    @GetMapping("/users")
    public ApiResponse<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = adminService.getAllUsers();
        return ApiResponse.<List<UserDTO>>builder()
                .data(users)
                .build();
    }

    @PostMapping("/users")
    public ApiResponse<AuthenticationResponse> createUser(@RequestBody UserCreationRequest userDTO) {
        AuthenticationResponse createdUser = adminService.createUser(userDTO);
        return ApiResponse.<AuthenticationResponse>builder()
                .data(createdUser)
                .message("User created")
                .build();
    }

    // Delete a user by ID
    @DeleteMapping("/users/{userId}")
    public ApiResponse<Void> deleteUserByID(@PathVariable Long userId) {
        adminService.deleteUserById(userId);
        return ApiResponse.<Void>builder()
                .message("User deleted")
                .build();
    }

    // Get all products
    @GetMapping("/products")
    public ApiResponse<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = adminService.getAllProducts();
        return ApiResponse.<List<ProductDto>>builder()
                .data(products)
                .build();
    }

    @PostMapping("/products")
    public ApiResponse<ProductDto> createProduct(@RequestBody ProductCreationRequest productRequest) {
        ProductDto createdProduct = adminService.createProduct(productRequest);
        return ApiResponse.<ProductDto>builder()
                .data(createdProduct)
                .message("Product created")
                .build();
    }

    // Delete a product by ID
    @DeleteMapping("/products/{productId}")
    public ApiResponse<Void> deleteProductByID(@PathVariable Long productId) {
        adminService.deleteProductById(productId);
        return ApiResponse.<Void>builder()
                .message("Product deleted")
                .build();
    }

    // Get all orders
    @GetMapping("/orders")
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = adminService.getAllOrders();
        return ApiResponse.<List<OrderResponse>>builder()
                .data(orders)
                .build();
    }

    // Delete an order by ID
    @DeleteMapping("/orders/{orderId}")
    public ApiResponse<Void> deleteOrder(@PathVariable Long orderId) {
        adminService.deleteOrderById(orderId);
        return ApiResponse.<Void>builder()
                .message("Order deleted")
                .build();
    }

    // ========== Category Management ==========

    // Get all categories
    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = adminService.getAllCategories();
        return ApiResponse.<List<CategoryResponse>>builder()
                .data(categories)
                .build();
    }

    // Create a new category
    @PostMapping("/categories")
    public ApiResponse<CategoryCreationRequest> createCategory(@RequestBody CategoryCreationRequest categoryDTO) {
        CategoryCreationRequest createdCategory = adminService.createCategory(categoryDTO);
        return ApiResponse.<CategoryCreationRequest>builder()
                .data(createdCategory)
                .message("Category created")
                .build();
    }

    // Delete a category by ID
    @DeleteMapping("/categories/{categoryId}")
    public ApiResponse<Void> deleteCategoryById(@PathVariable Long categoryId) {
        adminService.deleteCategoryById(categoryId);
        return ApiResponse.<Void>builder()
                .message("Category deleted")
                .build();
    }
    @PutMapping("/products/{productId}")
    public ApiResponse<ProductDto> updateProduct(@PathVariable Long productId, @RequestBody ProductCreationRequest productRequest) {
        logger.info("Request to update product with ID: {}", productId);
        ProductDto updatedProduct = adminService.updateProduct(productId, productRequest); // Delegate to ProductService
        return ApiResponse.<ProductDto>builder()
                .data(updatedProduct)
                .message("Product updated successfully")
                .build();
    }

    // Get category by ID
    @GetMapping("/categories/{categoryId}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        CategoryResponse categoryResponse = adminService.getCategoryById(categoryId);
        return ApiResponse.<CategoryResponse>builder()
                .data(categoryResponse)
                .build();
    }
}