package com.example.beskbd.services;

import com.example.beskbd.dto.object.CategoryDto;
import com.example.beskbd.dto.object.UserDTO;
import com.example.beskbd.dto.request.CategoryCreationRequest;
import com.example.beskbd.dto.request.ProductCreationRequest;
import com.example.beskbd.dto.request.UserCreationRequest;
import com.example.beskbd.dto.response.AuthenticationResponse;
import com.example.beskbd.dto.response.CategoryResponse;
import com.example.beskbd.dto.response.ProductDto;
import com.example.beskbd.dto.response.OrderResponse;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final CategoryService categoryService;

    /**
     * Get all users in the system.
     */
    public List<UserDTO> getAllUsers() {
        logger.info("Fetching all users");
        return userService.getAllUsers(); // Assume this method exists in UserService
    }

    /**
     * Delete a user by ID.
     */
    public void deleteUserById(Long userId) {
        logger.info("Soft deleting user with ID: {}", userId);
        userService.deleteUserById(userId); // Assume this method exists in UserService
    }

    /**
     * Get all products in the system.
     */
    public List<ProductDto> getAllProducts() {
        logger.info("Fetching all products");
        return productService.getAllProducts(); // Assume this method exists in ProductService
    }

    public ProductDto createProduct(ProductCreationRequest product) {
        logger.info("Creating new product: {}", product);
        return productService.addProduct(product); // Add product through ProductService
    }

    /**
     * Delete a product by ID.
     */
    public void deleteProductById(Long productId) {
        logger.info("Deleting product with ID: {}", productId);
        productService.deleteProductById(productId); // Assume this method exists in ProductService
    }
    public ProductDto updateProduct(Long product, ProductCreationRequest productRequest) {
        logger.info("Updating product: {}", product);
        return productService.updateProduct(product, productRequest); // Delegate to ProductService
    }

    /**
     * updateCategory.
     *
     */
    public CategoryDto updateCategory(CategoryCreationRequest categoryRequest) {
        logger.info("Updating category: {}", categoryRequest);
        return categoryService.updateCategory(categoryRequest); // Delegate to CategoryService
    }

    /**
     * Get all orders in the system.
     */
    public List<OrderResponse> getAllOrders() {
        logger.info("Fetching all orders");
        return orderService.getAllOrders(); // Assume this method exists in OrderService
    }

    /**
     * Delete an order by ID.
     */
    public void deleteOrderById(Long orderId) {
        logger.info("Deleting order with ID: {}", orderId);
        orderService.deleteOrderById(orderId); // Assume this method exists in OrderService
    }

    public AuthenticationResponse createUser(UserCreationRequest userDTO) {
        logger.info("Creating new user: {}", userDTO);
        return userService.createUser(userDTO); // Delegate to UserService
    }

    public CategoryCreationRequest createCategory(CategoryCreationRequest categoryDTO) {
        logger.info("Creating new category: {}", categoryDTO);
        return categoryService.createCategory(categoryDTO); // Assume this method exists in CategoryService
    }

    public List<CategoryResponse> getAllCategories() {
        logger.info("Fetching all categories");
        return categoryService.getAllCategories(); // Assume this method exists in CategoryService
    }

    public CategoryResponse getCategoryById(Long categoryId) {
        logger.info("Fetching category with ID: {}", categoryId);
        return categoryService.getCategoryById(categoryId); // Assume this method exists in CategoryService
    }

    public void deleteCategoryById(Long categoryId) {
        logger.info("Deleting category with ID: {}", categoryId);
        categoryService.deleteCategoryById(categoryId); // Assume this method exists in CategoryService
    }
}