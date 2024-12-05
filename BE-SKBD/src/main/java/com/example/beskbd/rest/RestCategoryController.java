package com.example.beskbd.rest;

import com.example.beskbd.dto.request.CategoryCreationRequest;
import com.example.beskbd.dto.response.ApiResponse;
import com.example.beskbd.dto.response.CategoryResponse; // Assuming you have a CategoryResponse
import com.example.beskbd.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class RestCategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreationRequest request) {
        // Adjusted to get result from service
        CategoryResponse categoryResponse = categoryService.createNewCategory(request);
        return ApiResponse.<CategoryResponse>builder()
                .data(categoryResponse)
                .success(true) // Indicating success
                .build();
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ApiResponse.<List<CategoryResponse>>builder()
                .success(true)
                .data(categories)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ApiResponse.<CategoryResponse>builder()
                .data(categoryService.getCategoryById(id))
                .success(true) // Indicating success
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategoryById(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return ApiResponse.<Void>builder()
                .data(null) // No content to return
                .success(true) // Indicating successful deletion
                .build();
    }
}