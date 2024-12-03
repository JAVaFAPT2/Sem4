package com.example.beskbd.services;

import com.example.beskbd.dto.request.CategoryCreationRequest;
import com.example.beskbd.dto.response.CategoryResponse;
import com.example.beskbd.entities.Category;
import com.example.beskbd.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createNewCategory(CategoryCreationRequest request) {
        try {
            Category.Gender gender = Category.Gender.valueOf(request.getGender().toUpperCase());
            Category category = Category.builder()
                    .gender(gender)
                    .description(request.getCategoryDescription())
                    .name(request.getCategoryName())
                    .productType(request.getProductType())
                    .build();
            category = categoryRepository.save(category); // Save category and return

            return convertToResponse(category); // Convert and return as response
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid gender value: " + request.getGender());
        }
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToResponse) // Mapping entity to DTO
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        return convertToResponse(category); // Return DTO
    }

    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    // Utility method to convert Category entity to CategoryResponse DTO
    private CategoryResponse convertToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId()) // assuming 'id' exists in your BaseEntity
                .name(category.getName())
                .description(category.getDescription())
                .gender(category.getGender().name()) // using name() to convert enum to String
                .productType(category.getProductType())
                .products(category.getProducts()) // Optionally return products
                .build();
    }
}